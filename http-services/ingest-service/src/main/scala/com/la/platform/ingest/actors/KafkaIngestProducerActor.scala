package com.la.platform.ingest.actors

import akka.NotUsed
import akka.actor.{Actor, ActorLogging, Props}
import akka.kafka.{ProducerMessage, ProducerSettings}
import akka.kafka.scaladsl.Producer
import akka.routing.FromConfig
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import com.la.platform.ingest.actors.KafkaIngestProducerActor.{DataIngested, IngestData}
import net.liftweb.json.DefaultFormats
import net.liftweb.json.Serialization.write
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{ByteArraySerializer, IntegerSerializer, StringSerializer}
import org.reactivestreams.Publisher
import rx.{Observable, RxReactiveStreams}
import rx.subjects.PublishSubject

/**
  * Created by zemi on 25/10/2016.
  */
class KafkaIngestProducerActor extends Actor with ActorLogging {

  implicit val formats = DefaultFormats

  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val producerSettings = ProducerSettings(context.system, new IntegerSerializer, new StringSerializer)
    .withBootstrapServers("localhost:9092")

  val kafkaProducer = producerSettings.createKafkaProducer()

  val subject = PublishSubject.create[IngestData]()

  val producerSource = Source.fromPublisher(publisher).map(element => {
    val now = java.time.LocalDateTime.now().toString
    val messageVal = write(KafkaIngestDataMessage(element.value, element.originator, now))
    log.debug(s"${getClass.getCanonicalName} produceData() -> message: $messageVal")
    new ProducerRecord[Integer, String]("IngestData", element.key, messageVal)
  }).runWith(Producer.plainSink(producerSettings, kafkaProducer))

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

}

object KafkaIngestProducerActor {

  case class IngestData(key: Int, value: String, originator: Option[String])

  case class DataIngested(value: String)

  val ACTOR_NAME = "kafkaIngestProducer"

  //  def props: Props = Props[KafkaIngestProducerActor]
  def props(): Props = FromConfig.props(Props(classOf[KafkaIngestProducerActor]))
}
