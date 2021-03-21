package com.la.platform.predict.actors.kafka.streams

import akka.Done
import akka.actor.{ActorRef, ActorSystem}
import akka.event.{Logging, LoggingAdapter}
import akka.kafka.ConsumerMessage.CommittableMessage
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.pattern.ask
import akka.stream.scaladsl.{Keep, Sink, Source}
import akka.stream.{ActorMaterializer, KillSwitches, UniqueKillSwitch}
import com.la.platform.predict.actors.kafka.PredictReloadModelKafkaConsumerActor
import com.la.platform.predict.actors.kafka.PredictReloadModelKafkaConsumerActor.PredictReloadModelJsonMsg
import com.la.platform.predict.actors.ml.PredictServiceActor
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer

import scala.concurrent.Future
import scala.util.{Failure, Success}

import spray.json._
import DefaultJsonProtocol._

/**
 * Created by zemi on 31/03/2018.
 */
trait ConsumerReloadModelStream {
  def subscription(): UniqueKillSwitch
}

private[streams] class ConsumerReloadModelStreamImpl(supervisor: ActorRef, implicit val system: ActorSystem)
  extends ConsumerReloadModelStream {

  protected val log: LoggingAdapter = Logging(system, getClass)

  implicit val materializer: ActorMaterializer = ActorMaterializer()

  protected implicit val executorService = system.dispatcher

  val bootstrap_servers: String = system.settings.config.getString("kafka.producer.bootstrap.servers")

  val consumerSettings: ConsumerSettings[String, String] = ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
    .withBootstrapServers(bootstrap_servers)
    .withGroupId("PredictData")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")

  val sourceStream: Source[CommittableMessage[String, String], Consumer.Control] = Consumer.committableSource(consumerSettings, Subscriptions.topics("PredictReloadModel"))

  val (killSwitches, done) = sourceStream.mapAsync(1) { msg =>
    val msgVal = msg.record.value()
    log.debug(s"Kafka consumer topic: predict-reload-model, message: $msgVal")
    processMessage(msgVal.parseJson.convertTo[PredictReloadModelJsonMsg])
    Future.successful(Done).map(_ => msg)
  }.mapAsync(1)(msg => msg.committableOffset.commitScaladsl())
    .viaMat(KillSwitches.single)(Keep.right)
    .toMat(Sink.ignore)(Keep.both)
    .run()

  done onComplete {
    case Success(s) => supervisor ! PredictReloadModelKafkaConsumerActor.Completed
    case Failure(e) => supervisor ! PredictReloadModelKafkaConsumerActor.Terminated
  }

  def processMessage(msg: PredictReloadModelJsonMsg): Unit = {
    system.eventStream.publish(PredictServiceActor.ReloadMlModel())
  }

  override def subscription(): UniqueKillSwitch = killSwitches
}
