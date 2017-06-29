package com.la.platform.predict.actors.kafka

import akka.Done
import akka.actor.{Actor, ActorLogging, Props}
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.kafka.scaladsl.Consumer
import akka.stream.{ActorMaterializer, IOResult}
import akka.stream.scaladsl.{Flow, Sink}
import com.la.platform.predict.actors.PredictActorSelection
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import net.liftweb.json._
import net.liftweb.json.Serialization.read

import scala.concurrent.Future

/**
  * Created by zemi on 03/11/2016.
  */
class PredictReloadModelKafkaConsumerActor extends Actor with ActorLogging with PredictActorSelection {

  import com.la.platform.predict.actors.ml.PredictServiceActor

  protected implicit val executorService = scala.concurrent.ExecutionContext.Implicits.global

  implicit val materializer = ActorMaterializer()

  val bootstrap_servers = context.system.settings.config.getString("kafka.producer.bootstrap.servers")

  implicit val formats = DefaultFormats

  val consumerSettings = ConsumerSettings(context.system, new StringDeserializer, new StringDeserializer)
    .withBootstrapServers(bootstrap_servers)
    .withGroupId("PredictData")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")

  val sourceStream = Consumer.committableSource(consumerSettings, Subscriptions.topics("PredictReloadModel"))
  sourceStream.mapAsync(1) { msg =>
      val msgVal = msg.record.value()
      log.debug(s"Kafka consumer topic: predict-reload-model, message: $msgVal")
      processPredictReloadModel(read[PredictReloadModelJsonMsg](msgVal))
      Future.successful(Done).map(_ => msg)
//      msg.committableOffset.commitScaladsl()
    }
    .mapAsync(1) { msg =>
      msg.committableOffset.commitScaladsl()
    }
    .runWith(Sink.ignore)


  override def receive: Receive = {
    case _ => {
      log.warning(s"Unsupported operation request from actor ${sender()}")
      sender ! PredictKafkaProducerUnsupportedOpr(s"sender: ${sender()}")
    }
  }

  /**
    * Process message from Kafka cluster
    * @param msg
    */
  def processPredictReloadModel(msg: PredictReloadModelJsonMsg): Unit = {
    Future {
      log.debug(s"${getClass.getCanonicalName} -> PredictReloadModelJsonMsg: $msg")
      selectPredictServiceActor ! PredictServiceActor.ReloadMlModel
    }
  }
}

object PredictReloadModelKafkaConsumerActor {

  val actor_name = "PredictKafkaProducerSupervisor"

  def props: Props = Props[PredictReloadModelKafkaConsumerActor]

}
