package com.la.platform.predict.rest

import java.util.UUID

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.pattern._
import akka.util.Timeout
import com.la.platform.common.rest.AbstractRestService
import com.la.platform.predict.actors.PredictActor
import com.la.platform.predict.actors.kafka.{PredictRequestMsg, PredictResponseMsg}

import scala.concurrent.Future
import scala.concurrent.duration._

/**
  * Created by zemi on 02/11/2016.
  */
class PredictRestService(implicit system: ActorSystem) extends AbstractRestService {

  override implicit val timeout: Timeout = 10000 milliseconds

  override def buildRoute(): Route =
    path("predict") {
      post {
        entity(as[PredictRequest]) {
          entity => complete(predict(entity))
        }
      } ~ get {
        complete("Ok")
      }
    }

  /**
    * Call prediction functionality
    *
    * @param predict
    * @return
    */
  def predict(predict: PredictRequest): Future[PredictResponse] = {
    getPredictActor.flatMap(predictAct => completeAndCleanUpAct({
        (predictAct ? PredictRequestMsg(predict.data)).mapTo[PredictResponseMsg]
          .map(responseMsg => PredictResponse(responseMsg.result))
      }, predictAct))
  }

  /**
    * Create PredictActor
    * @return actorRef
    */
  def getPredictActor: Future[ActorRef] = {
    Future {
      val uid = UUID.randomUUID().toString
      system.actorOf(PredictActor.props, s"PredictActor_$uid")
    }
  }
}
