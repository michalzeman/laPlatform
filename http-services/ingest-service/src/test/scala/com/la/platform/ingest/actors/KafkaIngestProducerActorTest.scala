package com.la.platform.ingest.actors

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import com.la.platform.common.actors.kafka.producer.ProducerFactory
import com.la.platform.common.settings.KafkaSettings
import com.la.platform.ingest.actors.KafkaIngestProducerActor.{DataIngested, IngestData}
import org.apache.kafka.clients.producer.KafkaProducer
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterAll, FunSuiteLike, Matchers}
import org.mockito.Mockito._

/**
  * Created by zemi on 07/11/2016.
  */
class KafkaIngestProducerActorTest extends TestKit(ActorSystem("ingest-data-http-service-KafkaIngestProducerActorTest"))
  with FunSuiteLike
  with BeforeAndAfterAll
  with Matchers
  with ImplicitSender
  with MockitoSugar {

  val producerFactory = mock[ProducerFactory[Int, String, KafkaSettings]]

//  test("send msg to kafka producer") {
//    val producer = mock[KafkaProducer[Int, String]]
//    when(producerFactory.getProducer).thenReturn(producer)
//    val predictKafkaProducerActor = system.actorOf(Props(classOf[KafkaIngestProducerActor], producerFactory))
//    predictKafkaProducerActor ! IngestData(0, "test data", None)
//    expectMsgType[DataIngested]
//  }

}
