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
import com.la.platform.ingest.streams.{PublisherStreamBuilder, ProducerStream}
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
class KafkaIngestProducerActor(publisherStreamBuilder: PublisherStreamBuilder) extends Actor with ActorLogging {

  var publisher: ProducerStream = _

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
  override def preStart(): Unit = {
    IngestEventBusExtension(context.system).eventBus.subscribe(self, classOf[IngestData])
    publisher = publisherStreamBuilder.create(ActorMaterializer(), context.system, self)
  }

  @scala.throws[Exception](classOf[Exception])
  override def postStop(): Unit = IngestEventBusExtension(context.system).eventBus.unsubscribe(self, classOf[IngestData])
}

object KafkaIngestProducerActor {

  case class IngestData(key: UUID, value: String, originator: Option[String], requester: ActorRef)

  case class DataIngested(value: String)

  val ACTOR_NAME = "kafkaIngestProducer"

    def props(publisherStreamBuilder: PublisherStreamBuilder): Props = Props(new KafkaIngestProducerActor(publisherStreamBuilder))
//  def props(): Props = FromConfig.props(Props(classOf[KafkaIngestProducerActor]))
}
