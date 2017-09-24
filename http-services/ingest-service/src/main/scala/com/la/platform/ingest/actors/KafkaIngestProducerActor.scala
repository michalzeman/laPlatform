package com.la.platform.ingest.actors

import java.util.UUID

import akka.Done
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.routing.FromConfig
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import com.la.platform.ingest.actors.KafkaIngestProducerActor.{DataIngested, IngestData}
import com.la.platform.ingest.bus.IngestEventBusExtension
import io.reactivex.processors.PublishProcessor
import net.liftweb.json.DefaultFormats
import net.liftweb.json.Serialization.write
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.{IntegerSerializer, StringSerializer}
import org.reactivestreams.Publisher

import scala.concurrent.Future

/**
  * Created by zemi on 25/10/2016.
  */
class KafkaIngestProducerActor extends Actor with ActorLogging {

  implicit val formats: DefaultFormats = DefaultFormats

  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val bootstrap_servers: String = context.system.settings.config.getString("kafka.producer.bootstrap.servers")

  val producerSettings: ProducerSettings[Integer, String] = ProducerSettings(context.system, new IntegerSerializer, new StringSerializer)
    .withBootstrapServers(bootstrap_servers)

  val kafkaProducer: KafkaProducer[Integer, String] = producerSettings.createKafkaProducer()

  val publisher: PublishProcessor[IngestData] = PublishProcessor.create[IngestData]()

  val producerSource: Future[Done] = Source.fromPublisher(publisher)
    .mapAsync(1)(mapMsg)
    .runWith(Producer.plainSink(producerSettings, kafkaProducer))


  private def mapMsg(element: IngestData): Future[ProducerRecord[Integer, String]] = {
    implicit val dispatcher = context.dispatcher
    Future {
      val now = java.time.LocalDateTime.now().toString
      val messageVal = write(KafkaIngestDataMessage(element.value, element.originator, now))
      log.debug(s"${getClass.getCanonicalName} produceData() -> message: $messageVal")
      new ProducerRecord[Integer, String]("IngestData", 1, messageVal)
    }
  }

  override def receive: Receive = {
    case msg:IngestData => sendMsgToKafka(msg)
    case _ => log.warning("problem !!!!!!!")
  }
  /**
    * Produce event into the kafka
    *
    * @param ingestData
    */
  def sendMsgToKafka(ingestData: IngestData): Unit = {
    log.info(s"${getClass.getCanonicalName} produceData() ->")
    publisher.onNext(ingestData)
    ingestData.requester ! DataIngested("OK")
  }

  @scala.throws[Exception](classOf[Exception])
  override def preStart(): Unit = IngestEventBusExtension(context.system).eventBus.subscribe(self, classOf[IngestData])

  @scala.throws[Exception](classOf[Exception])
  override def postStop(): Unit = IngestEventBusExtension(context.system).eventBus.unsubscribe(self, classOf[IngestData])
}

object KafkaIngestProducerActor {

  case class IngestData(key: UUID, value: String, originator: Option[String], requester: ActorRef)

  case class DataIngested(value: String)

  val ACTOR_NAME = "kafkaIngestProducer"

    def props: Props = Props[KafkaIngestProducerActor]
//  def props(): Props = FromConfig.props(Props(classOf[KafkaIngestProducerActor]))
}
