package com.la.platform.predict.actors

import akka.actor.{ActorRefFactory, ActorSelection}
import com.la.platform.predict.actors.kafka.{PredictKafkaProducerActor, PredictReloadModelKafkaConsumerActor}
import com.la.platform.predict.actors.ml.PredictServiceActor
import com.la.platform.predict.actors.supervisors.{PredictKafkaSupervisorActor, PredictSupervisorActor}

/**
  * Created by zemi on 03/11/2016.
  */
trait PredictActorSelection {

  val predictKafkaProducerActorPath = PredictKafkaSupervisorActor.getActorPath+"/"+PredictKafkaProducerActor.actor_name

  val predictResultKafkaConsumerActorPath = PredictKafkaSupervisorActor.getActorPath+"/"+PredictReloadModelKafkaConsumerActor.actor_name

  val predictPredictServiceActorPath = PredictSupervisorActor.getActorPath+"/"+PredictServiceActor.actor_name

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

  /**
    * find and return PredictServiceActor selection
    * @param context
    * @return ActorSelection
    */
  def selectPredictServiceActor(implicit context: ActorRefFactory): ActorSelection = context.actorSelection(predictPredictServiceActorPath)

}
