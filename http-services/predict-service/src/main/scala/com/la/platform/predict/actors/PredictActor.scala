package com.la.platform.predict.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.la.platform.predict.actors.PredictActor.{PredictRequestMsg, PredictResponseMsg}
import com.la.platform.predict.actors.kafka.{PredictKafkaConsumerMsg, PredictKafkaProducerMsg}

/**
  * Created by zemi on 03/11/2016.
  */
class PredictActor extends Actor with ActorLogging with PredictKafkaActorSelection {

  var client:ActorRef = _

  override def receive: Receive = {
    case PredictRequestMsg(data) => {
      client = sender
      selectPredictKafkaProducerActor ! PredictKafkaProducerMsg(data)
    }
    case PredictKafkaConsumerMsg(result) => {
      client ! PredictResponseMsg(result)
    }
  }

}

object PredictActor {

  case class PredictRequestMsg(data: String)

  case class PredictResponseMsg(result: String)

  def props: Props = Props[PredictActor]

}
