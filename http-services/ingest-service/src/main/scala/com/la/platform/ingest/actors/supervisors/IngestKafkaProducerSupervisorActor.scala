package com.la.platform.ingest.actors.supervisors

import java.util.concurrent.ExecutionException

import akka.actor.SupervisorStrategy.{Escalate, Restart}
import akka.actor.{Actor, ActorLogging, OneForOneStrategy, Props, SupervisorStrategy}
import com.la.platform.ingest.actors.KafkaIngestProducerActor
import org.apache.kafka.common.KafkaException

import scala.concurrent.duration._

/**
  * Created by zemi on 26/10/2016.
  */
class IngestKafkaProducerSupervisorActor extends Actor with ActorLogging {

  val kafkaIngestProducerActor = context.actorOf(KafkaIngestProducerActor.props, KafkaIngestProducerActor.ACTOR_NAME)

  context.watch(kafkaIngestProducerActor)

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

object IngestKafkaProducerSupervisorActor {

  val ACTOR_NAME = "ingestKafkaProducerSupervisorActor"

  def getActorPath:String = "/user/"+ACTOR_NAME

  def props: Props = Props[IngestKafkaProducerSupervisorActor]
}

case class CreateActorMsg(props: Props, name: String)
