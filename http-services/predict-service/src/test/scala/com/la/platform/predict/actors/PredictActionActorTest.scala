package com.la.platform.predict.actors

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import com.la.platform.predict.actors.ml.PredictServiceActor
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterAll, FunSuiteLike, Matchers}

/**
  * Created by zemi on 08/10/2017.
  */
class PredictActionActorTest extends TestKit(ActorSystem("predict-http-service-PredictActionActorTest"))
  with FunSuiteLike
  with BeforeAndAfterAll
  with Matchers
  with ImplicitSender
  with MockitoSugar {

  test("Action predict request/response test") {
    val predictActionActor = system.actorOf(PredictActionActor.props)

    predictActionActor ! PredictActionActor.PredictRequest("requestCmdTest")

    predictActionActor ! PredictServiceActor.PredictResponse("response from Service")

    expectMsg(PredictActionActor.PredictResponse("response from Service"))
  }

}
