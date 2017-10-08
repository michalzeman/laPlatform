package com.la.platform.predict.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.la.platform.predict.actors.PredictActionActor.{PredictRequest, PredictResponse}
import com.la.platform.predict.actors.ml.PredictServiceActor

/**
  * Created by zemi on 03/11/2016.
  */
class PredictActionActor extends Actor with ActorLogging {

  override def receive: Receive = {
    case PredictRequest(data) => {
      log.info(s"${self.path} -> PredictRequestMsg")
      context.system.eventStream.publish(PredictServiceActor.PredictRequest(data, self))
      context.become(waitingOnResponse(sender))
    }
  }

  private def waitingOnResponse(requester: ActorRef): Receive = {
    case PredictServiceActor.PredictResponse(result) => {
      log.info(s"${self.path} -> PredictResponseMsg")
      requester ! PredictResponse(result)
    }
  }

}

object PredictActionActor {

  case class PredictRequest(data: String)

  case class PredictResponse(result: String)

  def props: Props = Props[PredictActionActor]

}
