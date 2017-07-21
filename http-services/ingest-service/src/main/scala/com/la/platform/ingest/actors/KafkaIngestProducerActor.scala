package com.la.platform.ingest.actors

import java.util.UUID

import akka.Done
import akka.actor.{Actor, ActorLogging, Props}
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.routing.FromConfig
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import com.la.platform.ingest.actors.KafkaIngestProducerActor.{DataIngested, IngestData}
import com.la.platform.ingest.bus.IngestEventBusExtension
import net.liftweb.json.DefaultFormats
import net.liftweb.json.Serialization.write
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.{IntegerSerializer, StringSerializer}
import org.reactivestreams.Publisher
import rx.RxReactiveStreams
import rx.subjects.PublishSubject

import scala.concurrent.Future

/**
  * Created by zemi on 25/10/2016.
  */
class KafkaIngestProducerActor extends Actor with ActorLogging {

  implicit val formats = DefaultFormats

  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val bootstrap_servers: String = context.system.settings.config.getString("kafka.producer.bootstrap.servers")

  val producerSettings: ProducerSettings[Integer, String] = ProducerSettings(context.system, new IntegerSerializer, new StringSerializer)
    .withBootstrapServers(bootstrap_servers)

  val kafkaProducer: KafkaProducer[Integer, String] = producerSettings.createKafkaProducer()

  val subject: PublishSubject[IngestData] = PublishSubject.create[IngestData]()

  val producerSource: Future[Done] = Source.fromPublisher(publisher).map(mapMsg).runWith(Producer.plainSink(producerSettings, kafkaProducer))


  private def mapMsg(element: IngestData): ProducerRecord[Integer, String] = {
    val now = java.time.LocalDateTime.now().toString
    val messageVal = write(KafkaIngestDataMessage(element.value, element.originator, now))
    log.debug(s"${getClass.getCanonicalName} produceData() -> message: $messageVal")
    new ProducerRecord[Integer, String]("IngestData", 1, messageVal)
  }

  def publisher: Publisher[IngestData] = {
    RxReactiveStreams.toPublisher[IngestData](subject)
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
    subject.onNext(ingestData)
    sender ! DataIngested("OK")
  }

  @scala.throws[Exception](classOf[Exception])
  override def preStart(): Unit = IngestEventBusExtension(context.system).eventBus.subscribe(self, classOf[IngestData])

  @scala.throws[Exception](classOf[Exception])
  override def postStop(): Unit = IngestEventBusExtension(context.system).eventBus.unsubscribe(self, classOf[IngestData])
}

object KafkaIngestProducerActor {

  case class IngestData(key: UUID, value: String, originator: Option[String])

  case class DataIngested(value: String)

  val ACTOR_NAME = "kafkaIngestProducer"

    def props: Props = Props[KafkaIngestProducerActor]
//  def props(): Props = FromConfig.props(Props(classOf[KafkaIngestProducerActor]))
}
