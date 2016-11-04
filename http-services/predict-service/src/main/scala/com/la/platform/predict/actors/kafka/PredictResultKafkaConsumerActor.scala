package com.la.platform.predict.actors.kafka

import akka.Done
import akka.actor.{Actor, ActorLogging, Props}
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.kafka.scaladsl.Consumer
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import com.la.platform.predict.actors.PredictActor.PredictResponseMsg
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer

import scala.concurrent.Future

/**
  * Created by zemi on 03/11/2016.
  */
class PredictResultKafkaConsumerActor extends Actor with ActorLogging {

  protected implicit val executorService = scala.concurrent.ExecutionContext.Implicits.global

  implicit val materializer = ActorMaterializer()

  val consumerSettings = ConsumerSettings(context.system, new StringDeserializer, new StringDeserializer)
    .withBootstrapServers("localhost:9092")
    .withGroupId("PredictData")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
//
//  val sourceStream = Consumer.committableSource(consumerSettings, Subscriptions.topics("prediction-result"))
//    .mapAsync(1) { msg =>
//      val msgVal = msg.record.value()
//      log.debug(s"Kafka consumer topic: prediction-result, message: $msgVal")
//      Future.successful(Done).map(_ => msg)
////      msg.committableOffset.commitScaladsl()
//    }
//    .mapAsync(1) { msg =>
//      msg.committableOffset.commitScaladsl()
//    }
//    .runWith(Sink.ignore)


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
  def processPredictionMeg(msg: PredictionJsonMsg): Unit = {
    Future {
      val orgPath = context.actorSelection(msg.sender)
      orgPath ! PredictResponseMsg(msg.data)
    }
  }
}

object PredictResultKafkaConsumerActor {

  val actor_name = "PredictKafkaProducerSupervisor"

  def props: Props = Props[PredictResultKafkaConsumerActor]

}
