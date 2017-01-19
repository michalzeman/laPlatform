package com.la.platform.predict.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.la.platform.predict.actors.ml.PredictServiceActor

/**
  * Created by zemi on 03/11/2016.
  */
class PredictActionActor extends Actor with ActorLogging with PredictActorSelection {

  var client:ActorRef = _

  override def receive: Receive = {
    case msg:PredictServiceActor.PredictRequestMsg => {
      client = sender
      selectPredictServiceActor ! msg
    }
    case msg:PredictServiceActor.PredictResponseMsg => {
      client ! msg
    }
  }

}

object PredictActionActor {

  def props: Props = Props[PredictActionActor]

}
