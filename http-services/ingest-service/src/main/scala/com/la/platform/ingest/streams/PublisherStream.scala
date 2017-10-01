package com.la.platform.ingest.streams

import akka.Done
import akka.actor.{ActorRef, ActorSystem}
import akka.event.{Logging, LoggingAdapter}
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import com.la.platform.ingest.actors.KafkaIngestDataMessage
import com.la.platform.ingest.actors.KafkaIngestProducerActor.IngestData
import io.reactivex.processors.PublishProcessor
import net.liftweb.json.DefaultFormats
import net.liftweb.json.Serialization.write
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.{IntegerSerializer, StringSerializer}
import org.reactivestreams.Publisher

import scala.concurrent.{ExecutionContextExecutor, Future}

/**
  * Created by zemi on 28/09/2017.
  */
trait PublisherStream {

  def onNext(value: IngestData): Unit

}

object PublisherStream {
  def apply(materializer: ActorMaterializer, system: ActorSystem, supervisor: ActorRef): PublisherStream = new PublisherStreamImpl(system, supervisor, materializer)
}

/**
  * Created by zemi on 28/09/2017.
  */
private class PublisherStreamImpl(system: ActorSystem, supervisor: ActorRef, implicit val materializer: ActorMaterializer) extends PublisherStream {

  protected val log: LoggingAdapter = Logging(system, getClass)

  protected implicit val executorService: ExecutionContextExecutor = system.dispatcher

  implicit val formats: DefaultFormats = DefaultFormats

  private val bootstrap_servers: String = system.settings.config.getString("kafka.producer.bootstrap.servers")

  private val producerSettings: ProducerSettings[Integer, String] = ProducerSettings(system, new IntegerSerializer, new StringSerializer)
    .withBootstrapServers(bootstrap_servers)

  private val kafkaProducer: KafkaProducer[Integer, String] = producerSettings.createKafkaProducer()

  private val publisher: PublishProcessor[IngestData] = PublishProcessor.create[IngestData]()

  private val producerSource: Future[Done] = Source.fromPublisher(publisher)
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

}
