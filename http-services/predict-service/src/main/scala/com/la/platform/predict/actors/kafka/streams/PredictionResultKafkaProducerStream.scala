package com.la.platform.predict.actors.kafka.streams

import akka.actor.{ActorRef, ActorSystem}
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.ActorMaterializer
import com.la.platform.common.streams.AbstractKafkaProducerStream
import com.la.platform.predict.actors.ml.PredictServiceActor
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{IntegerSerializer, StringSerializer}

import scala.concurrent.Future

import spray.json._
import DefaultJsonProtocol._

/**
 * Created by zemi on 03/04/2018.
 */
trait PredictionResultKafkaProducerStream {
  def onNext(value: PredictServiceActor.PredictionResult): Unit
}

private[streams] class PredictionResultKafkaProducerStreamImpl
(system: ActorSystem, supervisor: ActorRef)(implicit materializer: ActorMaterializer)
  extends AbstractKafkaProducerStream[PredictServiceActor.PredictionResult, Integer, String](system, supervisor)
    with PredictionResultKafkaProducerStream {

  val topic: String = system.settings.config.getString("kafka.producer.prediction.topic")

  producerSource
    .mapAsync(1)(msg => {
      Future {
        val kafkaMsg = msg.toJson.toString()
        log.debug(s"${getClass.getCanonicalName} produceData() -> message: $kafkaMsg")
        new ProducerRecord[Integer, String](topic, 1, kafkaMsg)
      }
    })
    .runWith(Producer.plainSink(producerSettings, kafkaProducer))

  override protected def getBootstrapServers: String = system.settings.config.getString("kafka.producer.bootstrap.servers")

  override def getProducerSettings: ProducerSettings[Integer, String] =
    ProducerSettings(system, new IntegerSerializer, new StringSerializer)

  def onNext(value: PredictServiceActor.PredictionResult): Unit = {
    publisher.onNext(value)
  }
}