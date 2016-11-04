package com.la.platform.predict.actors

import akka.actor.{ActorRefFactory, ActorSelection}
import com.la.platform.predict.actors.kafka.{PredictKafkaProducerActor, PredictResultKafkaConsumerActor}
import com.la.platform.predict.actors.supervisors.PredictKafkaSupervisorActor

/**
  * Created by zemi on 03/11/2016.
  */
trait PredictKafkaActorSelection {

  val predictKafkaProducerActorPath = PredictKafkaSupervisorActor.getActorPath+"/"+PredictKafkaProducerActor.actor_name

  val predictResultKafkaConsumerActorPath = PredictKafkaSupervisorActor.getActorPath+"/"+PredictResultKafkaConsumerActor.actor_name

  /**
    * find and return PredictKafkaProducerActor selection
    * @param context
    * @return ActorSelection
    */
  def selectPredictKafkaProducerActor(implicit context: ActorRefFactory): ActorSelection = context.actorSelection(predictKafkaProducerActorPath)

  /**
    * find and return PredictResultKafkaConsumerActor selection
    * @param context
    * @return ActorSelection
    */
  def selectPredictResultKafkaConsumerActor(implicit context: ActorRefFactory): ActorSelection = context.actorSelection(predictResultKafkaConsumerActorPath)

}
