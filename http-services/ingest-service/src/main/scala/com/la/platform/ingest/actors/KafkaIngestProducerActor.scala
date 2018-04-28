package com.la.platform.ingest.actors

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.stream.ActorMaterializer
import com.la.platform.ingest.actors.KafkaIngestProducerActor.{DataIngested, IngestData}
import com.la.platform.ingest.bus.IngestEventBusExtension
import com.la.platform.ingest.streams.{PublisherStream, PublisherStreamBuilder}

/**
  * Created by zemi on 25/10/2016.
  */
class KafkaIngestProducerActor(publisherStreamBuilder: PublisherStreamBuilder) extends Actor with ActorLogging {

  var publisher: Option[PublisherStream] = None

  override def receive: Receive = {
    case msg: IngestData => sendMsgToKafka(msg)
    case _ => log.warning("problem !!!!!!!")
  }

  /**
    * Produce event into the kafka
    *
    * @param ingestData
    */
  def sendMsgToKafka(ingestData: IngestData): Unit = {
    log.info(s"${getClass.getCanonicalName} produceData() ->")
    publisher.foreach(_.onNext(ingestData))
    ingestData.requester ! DataIngested("OK")
  }

  @scala.throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    IngestEventBusExtension(context.system).eventBus.subscribe(self, classOf[IngestData])
    publisher = Some(publisherStreamBuilder.build(ActorMaterializer(), context.system, self))
  }

  @scala.throws[Exception](classOf[Exception])
  override def postStop(): Unit = IngestEventBusExtension(context.system).eventBus.unsubscribe(self, classOf[IngestData])
}

object KafkaIngestProducerActor {

  case class IngestData(key: UUID, value: String, originator: Option[String], requester: ActorRef)

  case class DataIngested(value: String)

  val ACTOR_NAME = "kafkaIngestProducer"

  def props(publisherStreamBuilder: PublisherStreamBuilder): Props = Props(new KafkaIngestProducerActor(publisherStreamBuilder))
}
