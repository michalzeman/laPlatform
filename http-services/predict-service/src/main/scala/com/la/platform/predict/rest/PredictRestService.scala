package com.la.platform.predict.rest

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import com.la.platform.rest.common.AbstractRestService

import scala.concurrent.Future

/**
  * Created by zemi on 02/11/2016.
  */
class PredictRestService(implicit system: ActorSystem) extends AbstractRestService {
  override def buildRoute(): Route =
    path("predict") {
      post {
        entity(as[PredictRequest]) {
          entity => complete("Ok")
        }
      } ~ get {
        complete("Ok")
      }
    }

  /**
    * Call prediction functionality
    * @param predict
    * @return
    */
  def predict(predict: PredictRequest): Future[PredictResponse] = {
    completeAndCleanUpAct( {
      Future {
        PredictResponse("OK")
      }
    })
  }
}
