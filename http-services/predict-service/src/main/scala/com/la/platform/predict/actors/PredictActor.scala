package com.la.platform.predict.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.la.platform.predict.actors.kafka.{PredictResponseMsg, PredictRequestMsg}

/**
  * Created by zemi on 03/11/2016.
  */
class PredictActor extends Actor with ActorLogging with PredictKafkaActorSelection {

  var client:ActorRef = _

  override def receive: Receive = {
    case msg:PredictRequestMsg => {
      client = sender
      selectPredictKafkaProducerActor ! msg
    }
    case msg:PredictResponseMsg => {
      client ! msg
    }
  }

}

object PredictActor {

  def props: Props = Props[PredictActor]

}
