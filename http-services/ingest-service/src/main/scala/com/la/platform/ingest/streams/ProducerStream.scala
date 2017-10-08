package com.la.platform.ingest.streams

import akka.{Done, NotUsed}
import akka.actor.{ActorRef, ActorSystem}
import akka.event.{Logging, LoggingAdapter}
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import com.la.platform.common.streams.AbstractKafkaProducerStream
import com.la.platform.ingest.actors.KafkaIngestDataMessage
import com.la.platform.ingest.actors.KafkaIngestProducerActor.IngestData
import io.reactivex.processors.PublishProcessor
import net.liftweb.json.DefaultFormats
import net.liftweb.json.Serialization.write
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.{IntegerSerializer, StringSerializer}

import scala.concurrent.{ExecutionContextExecutor, Future}

/**
  * Created by zemi on 28/09/2017.
  */
trait ProducerStream {

  def onNext(value: IngestData): Unit

}

object ProducerStream {
  def apply(materializer: ActorMaterializer, system: ActorSystem, supervisor: ActorRef): ProducerStream = new ProducerStreamImpl(system, supervisor, materializer)
}

/**
  * Created by zemi on 28/09/2017.
  */
private class ProducerStreamImpl(system: ActorSystem, supervisor: ActorRef, override implicit val materializer: ActorMaterializer)
  extends AbstractKafkaProducerStream[IngestData, Integer, String](system, supervisor, materializer)
    with ProducerStream  {

  implicit val formats: DefaultFormats = DefaultFormats

  producerSource
    .mapAsync(1)(mapMsg)
    .runWith(Producer.plainSink(producerSettings, kafkaProducer))

  private def mapMsg(element: IngestData): Future[ProducerRecord[Integer, String]] = {
    Future {
      val now = java.time.LocalDateTime.now().toString
      val messageVal = write(KafkaIngestDataMessage(element.value, element.originator, now))
      log.debug(s"${getClass.getCanonicalName} produceData() -> message: $messageVal")
      new ProducerRecord[Integer, String]("IngestData", 1, messageVal)
    }
  }

  def onNext(value: IngestData): Unit = {
    publisher.onNext(value)
  }

  override protected def getBootstrapServers: String = system.settings.config.getString("kafka.producer.bootstrap.servers")

  override def getProducerSettings: ProducerSettings[Integer, String] =
    ProducerSettings(system, new IntegerSerializer, new StringSerializer)
}
