package com.la.platform.predict.actors

import akka.actor.{ActorRefFactory, ActorSelection}
import com.la.platform.predict.actors.kafka.{PredictionResultKafkaProducerActor, PredictReloadModelKafkaConsumerActor}
import com.la.platform.predict.actors.ml.PredictServiceActor
import com.la.platform.predict.actors.supervisors.{PredictKafkaSupervisorActor, PredictSupervisorActor}

/**
  * Created by zemi on 03/11/2016.
  */
trait PredictActorSelection {

  val predictionResultKafkaProducerActorPath:String = PredictKafkaSupervisorActor.getActorPath+"/"+PredictionResultKafkaProducerActor.actor_name

  val predictReloadModelKafkaConsumerActorPath:String = PredictKafkaSupervisorActor.getActorPath+"/"+PredictReloadModelKafkaConsumerActor.actor_name

  val predictPredictServiceActorPath:String = PredictSupervisorActor.getActorPath+"/"+PredictServiceActor.actor_name

  /**
    * find and return PredictionResultKafkaProducerActor selection
    * @param context
    * @return ActorSelection
    */
  def selectPredictionResultKafkaProducerActor(implicit context: ActorRefFactory): ActorSelection = context.actorSelection(predictionResultKafkaProducerActorPath)

  /**
    * find and return PredictReloadModelKafkaConsumerActor selection
    * @param context
    * @return ActorSelection
    */
  def selectPredictReloadModelKafkaConsumerActor(implicit context: ActorRefFactory): ActorSelection = context.actorSelection(predictReloadModelKafkaConsumerActorPath)

  /**
    * find and return PredictServiceActor selection
    * @param context
    * @return ActorSelection
    */
  def selectPredictServiceActor(implicit context: ActorRefFactory): ActorSelection = context.actorSelection(predictPredictServiceActorPath)

}
