package com.la.platform.predict.actors.kafka

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterAll, FunSuiteLike, Matchers}
import org.mockito.Mockito._


/**
  * Created by zemi on 07/11/2016.
  */
class PredictKafkaProducerActorTest extends TestKit(ActorSystem("predict-http-service-PredictKafkaProducerActorTest"))
  with FunSuiteLike
  with BeforeAndAfterAll
  with Matchers
  with ImplicitSender
  with MockitoSugar {

//  val producerFactory = mock[PredictionResultKafkaProducerFactory]
//
//  test("send msg to kafka producer") {
//    val producer = mock[KafkaProducer[Int, String]]
//    when(producerFactory.getProducer).thenReturn(producer)
//    val predictKafkaProducerActor = system.actorOf(Props(classOf[PredictionResultKafkaProducerActor], producerFactory))
//    predictKafkaProducerActor ! PredictServiceActor.PredictRequestMsg("test data")
//    expectMsg(PredictRequestMsgSent)
//  }

}
