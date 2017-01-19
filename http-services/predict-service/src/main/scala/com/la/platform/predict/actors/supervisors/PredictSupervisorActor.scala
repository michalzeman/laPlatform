package com.la.platform.predict.actors.supervisors

import java.util.concurrent.ExecutionException

import akka.actor.{Actor, ActorLogging, OneForOneStrategy, Props, SupervisorStrategy}
import akka.actor.SupervisorStrategy.{Escalate, Restart}
import com.la.platform.predict.actors.ml.PredictServiceActor

import scala.concurrent.duration._

/**
  * Created by zemi on 14/12/2016.
  */
class PredictSupervisorActor extends Actor with ActorLogging {

  val predictService = context.actorOf(PredictServiceActor.props, PredictServiceActor.actor_name)

  context.watch(predictService)

  override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
    case _: NullPointerException      => Restart
    case _: InterruptedException      => Restart
    case _: ExecutionException      => Restart
    case _: RuntimeException => Restart
    case _: Exception                 => Escalate
  }

  override def receive: Receive = {
    case PredictSupervisorActor.CreateActorMsg(props, name) => sender ! context.actorOf(props, name)
    case props:Props => sender ! context.actorOf(props)
  }

}

object PredictSupervisorActor {

  case class CreateActorMsg(props: Props, name: String)

  val actor_name = "PredictSupervisorActor"

  def getActorPath: String = "/user/" + actor_name

  def props: Props = Props[PredictSupervisorActor]
}

