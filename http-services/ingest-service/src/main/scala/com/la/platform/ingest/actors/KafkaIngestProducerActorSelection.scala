package com.la.platform.ingest.actors

import akka.actor.{ActorRefFactory, ActorSelection}
import com.la.platform.ingest.actors.supervisors.IngestKafkaProducerSupervisorActor

/**
  * Created by zemi on 26/10/2016.
  */
trait KafkaIngestProducerActorSelection {

  val actorPath = IngestKafkaProducerSupervisorActor.getActorPath+"/"+KafkaIngestProducerActor.ACTOR_NAME

  /**
    * find and return KafkaIngestProducerActor selection
    * @param context
    * @return ActorSelection
    */
  def select(implicit context: ActorRefFactory): ActorSelection = context.actorSelection(actorPath)

}
