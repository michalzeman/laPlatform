package com.la.platform.predict.actors.kafka

import java.util.UUID

import akka.actor.{Actor, ActorLogging, Props}
import com.la.platform.predict.actors.kafka.PredictReloadModelKafkaConsumerActor.Terminated
import com.la.platform.predict.actors.kafka.streams.{ConsumerReloadModelStream, ConsumerReloadModelStreamBuilder}

/**
  * Created by zemi on 03/11/2016.
  */
class PredictReloadModelKafkaConsumerActor(consumerReloadModelStreamBuilder: ConsumerReloadModelStreamBuilder)
  extends Actor with ActorLogging {

  val predictServiceCorrelationId: UUID = UUID.randomUUID()


  var consumerReloadModelStream: Option[ConsumerReloadModelStream] = None

  override def preStart(): Unit = {
    super.preStart()
    consumerReloadModelStream = Some(consumerReloadModelStreamBuilder.build(context.system, self))
  }

  override def postStop(): Unit = {
    super.postStop()
    consumerReloadModelStream.foreach(_.subscription().shutdown())
  }

  override def receive: Receive = {
    case Terminated => consumerReloadModelStream = Some(consumerReloadModelStreamBuilder.build(context.system, self))
    case _ => {
      log.warning(s"Unsupported operation request from actor ${sender()}")
      sender ! PredictKafkaProducerUnsupportedOpr(s"sender: ${sender()}")
    }
  }
}

object PredictReloadModelKafkaConsumerActor {

  case object Completed

  case object Terminated

  case class PredictReloadModelJsonMsg(data: String, sender: String)

  val actor_name = "PredictKafkaProducerSupervisor"

  def props(consumerReloadModelStreamBuilder: ConsumerReloadModelStreamBuilder): Props =
    Props(new PredictReloadModelKafkaConsumerActor(consumerReloadModelStreamBuilder))

}
