package com.la.platform.ingest.actors

import java.util.concurrent.ExecutionException

import akka.actor.Actor.Receive
import akka.actor.SupervisorStrategy.{Escalate, Restart}
import akka.actor.{Actor, ActorLogging, OneForOneStrategy, SupervisorStrategy}
import org.apache.kafka.common.KafkaException

import scala.concurrent.duration._

/**
  * Created by zemi on 21/05/2017.
  */
class KafkaProducerManager extends Actor with ActorLogging {

  override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
    case _: NullPointerException      => Restart
    case _: InterruptedException      => Restart
    case _: ExecutionException      => Restart
    case _: KafkaException      => Restart
    case _: RuntimeException => Restart
    case _: Exception                 => Escalate
  }

  override def receive: Receive = ???
}
