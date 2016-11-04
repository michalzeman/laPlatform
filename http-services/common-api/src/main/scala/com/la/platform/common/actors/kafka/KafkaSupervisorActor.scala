package com.la.platform.common.actors.kafka

import java.util.concurrent.ExecutionException

import akka.actor.SupervisorStrategy.{Escalate, Restart}
import akka.actor.{Actor, ActorLogging, OneForOneStrategy, Props, SupervisorStrategy}
import org.apache.kafka.common.KafkaException
import scala.concurrent.duration._

/**
  * Created by zemi on 03/11/2016.
  */
trait KafkaSupervisorActor extends Actor with ActorLogging {


  override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
    case _: NullPointerException      => Restart
    case _: InterruptedException      => Restart
    case _: ExecutionException      => Restart
    case _: KafkaException      => Restart
    case _: RuntimeException => Restart
    case _: Exception                 => Escalate
  }

  override def receive: Receive = {
    case CreateActorMsg(props, name) => sender ! context.actorOf(props, name)
    case props:Props => sender ! context.actorOf(props)
  }

}

case class CreateActorMsg(props: Props, name: String)
