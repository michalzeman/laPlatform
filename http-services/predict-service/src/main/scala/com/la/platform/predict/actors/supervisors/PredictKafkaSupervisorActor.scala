package com.la.platform.predict.actors.supervisors

import akka.actor.{ActorRef, Props}
import com.la.platform.common.actors.kafka.KafkaSupervisorActor
import com.la.platform.predict.actors.kafka.{PredictReloadModelKafkaConsumerActor, PredictionResultKafkaProducerActor, PredictionResultKafkaProducerFactory}

/**
  * Created by zemi on 03/11/2016.
  */
class PredictKafkaSupervisorActor extends KafkaSupervisorActor {

  val predictionResultKafkaProducer: ActorRef = context.actorOf(PredictionResultKafkaProducerActor
    .props(
      PredictionResultKafkaProducerFactory(context.system.settings.config)),
    PredictionResultKafkaProducerActor.actor_name)

  context.watch(predictionResultKafkaProducer)

  val predictReloadModelKafkaConsumerActor: ActorRef = context.actorOf(PredictReloadModelKafkaConsumerActor.props,
    PredictReloadModelKafkaConsumerActor.actor_name)

  context.watch(predictReloadModelKafkaConsumerActor)

}

object PredictKafkaSupervisorActor {

  val ACTOR_NAME = "PredictKafkaSupervisor"

  def getActorPath: String = "/user/" + ACTOR_NAME

  def props: Props = Props[PredictKafkaSupervisorActor]

}