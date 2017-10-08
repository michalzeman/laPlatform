package com.la.platform.predict.actors.ml

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit}
import com.la.platform.predict.actors.PredictActionActor
import org.mockito.Matchers.any
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterAll, FunSuiteLike, Matchers}

/**
  * Created by zemi on 08/10/2017.
  */
class PredictServiceActorTest extends TestKit(ActorSystem("predict-http-service-PredictServiceActorTest"))
  with FunSuiteLike
  with BeforeAndAfterAll
  with Matchers
  with ImplicitSender
  with MockitoSugar {

  test("predict command test") {
    val logisticRegressionProviderBuilder = mock[LogisticRegressionProviderBuilder]
    val logisticRegressionProvider = mock[LogisticRegressionProvider]

    when(logisticRegressionProviderBuilder.build(any[ActorRef], any[ActorSystem])).thenReturn(logisticRegressionProvider)
    when(logisticRegressionProvider.predict(any[String])).thenReturn(Some("0.0"))

    system.actorOf(PredictServiceActor.props(logisticRegressionProviderBuilder))

    val predictActionActor = system.actorOf(PredictActionActor.props)

    predictActionActor ! PredictActionActor.PredictRequest("requestCmdTest")

    expectMsg(PredictActionActor.PredictResponse("0.0"))
  }

  test("Load ML model test") {
    val logisticRegressionProviderBuilder = mock[LogisticRegressionProviderBuilder]
    val logisticRegressionProvider = mock[LogisticRegressionProvider]

    when(logisticRegressionProviderBuilder.build(any[ActorRef], any[ActorSystem])).thenReturn(logisticRegressionProvider)

    val predictServiceActor =  system.actorOf(PredictServiceActor.props(logisticRegressionProviderBuilder))
    predictServiceActor ! PredictServiceActor.ReloadMlModel()

    verify(logisticRegressionProvider, org.mockito.Mockito.atLeast(1)).loadMlModel()
  }

}
