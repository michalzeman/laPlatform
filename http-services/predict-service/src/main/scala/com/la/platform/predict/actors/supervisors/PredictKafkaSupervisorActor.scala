package com.la.platform.predict.actors.supervisors

import akka.actor.{ActorRef, Props}
import com.la.platform.common.actors.kafka.KafkaSupervisorActor
import com.la.platform.predict.actors.kafka.streams.{ConsumerReloadModelStreamBuilder, PredictionResultKafkaProducerStreamBuilder}
import com.la.platform.predict.actors.kafka.{PredictReloadModelKafkaConsumerActor, PredictionResultKafkaProducerActor}

/**
  * Created by zemi on 03/11/2016.
  */
class PredictKafkaSupervisorActor extends KafkaSupervisorActor {

  val predictionResultKafkaProducer: ActorRef = context.actorOf(PredictionResultKafkaProducerActor
    .props(PredictionResultKafkaProducerStreamBuilder.builder()), PredictionResultKafkaProducerActor.actor_name)

  context.watch(predictionResultKafkaProducer)

  val predictReloadModelKafkaConsumerActor: ActorRef = context.actorOf(PredictReloadModelKafkaConsumerActor.props(ConsumerReloadModelStreamBuilder.builder),
    PredictReloadModelKafkaConsumerActor.actor_name)

  context.watch(predictReloadModelKafkaConsumerActor)

}

object PredictKafkaSupervisorActor {

  val ACTOR_NAME = "PredictKafkaSupervisor"

  def getActorPath: String = "/user/" + ACTOR_NAME

  def props: Props = Props[PredictKafkaSupervisorActor]

}