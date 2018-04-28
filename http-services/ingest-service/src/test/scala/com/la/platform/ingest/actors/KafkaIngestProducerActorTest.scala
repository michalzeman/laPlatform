package com.la.platform.ingest.actors

import akka.actor.{ActorRef, ActorSystem}
import akka.stream.ActorMaterializer
import akka.testkit.{ImplicitSender, TestKit}
import com.la.platform.ingest.rest.Ingest
import com.la.platform.ingest.streams.{PublisherStream, PublisherStreamBuilder}
import org.mockito.Matchers.any
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterAll, FunSuiteLike, Matchers}

/**
  * Created by zemi on 07/11/2016.
  */
class KafkaIngestProducerActorTest extends TestKit(ActorSystem("ingest-data-http-service-KafkaIngestProducerActorTest"))
  with FunSuiteLike
  with BeforeAndAfterAll
  with Matchers
  with ImplicitSender
  with MockitoSugar {


  test("send msg to kafka producer") {

    val publisherStreamBuilder = mock[PublisherStreamBuilder]
    val publisherStream = mock[PublisherStream]
    when(publisherStreamBuilder.build(any[ActorMaterializer], any[ActorSystem], any[ActorRef])).thenReturn(publisherStream)

    val kafkaProducer = system.actorOf(KafkaIngestProducerActor.props(publisherStreamBuilder))

    val ingestActionActor = system.actorOf(IngestActionActor.props)

    ingestActionActor ! Ingest("Test", Some("testOriginator"))

    expectMsgType[IngestActionActor.IngestResponse]
  }
}
