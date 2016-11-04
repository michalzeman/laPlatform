package com.la.platform.predict.actors.supervisors

import akka.actor.Props
import com.la.platform.common.actors.kafka.KafkaSupervisorActor
import com.la.platform.predict.actors.kafka.{PredictKafkaProducerActor, PredictResultKafkaConsumerActor}

/**
  * Created by zemi on 03/11/2016.
  */
class PredictKafkaSupervisorActor extends KafkaSupervisorActor {

  val predictKafkaProducer = context.actorOf(PredictKafkaProducerActor.props, PredictKafkaProducerActor.actor_name)

  context.watch(predictKafkaProducer)

  val predictResultKafkaConsumer = context.actorOf(PredictResultKafkaConsumerActor.props, PredictResultKafkaConsumerActor.actor_name)

  context.watch(predictResultKafkaConsumer)

}

object PredictKafkaSupervisorActor {

  val ACTOR_NAME = "PredictKafkaSupervisor"

  def getActorPath:String = "/user/"+ACTOR_NAME

  def props: Props = Props[PredictKafkaSupervisorActor]

}