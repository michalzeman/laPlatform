package com.la.platform.ingest.actors.supervisors

import akka.actor.Props
import com.la.platform.common.actors.kafka.KafkaSupervisorActor
import com.la.platform.ingest.actors.KafkaIngestProducerActor

/**
  * Created by zemi on 26/10/2016.
  */
class IngestKafkaProducerSupervisorActor extends KafkaSupervisorActor {

  val kafkaIngestProducerActor = context.actorOf(KafkaIngestProducerActor.props, KafkaIngestProducerActor.ACTOR_NAME)

  context.watch(kafkaIngestProducerActor)

}

object IngestKafkaProducerSupervisorActor {

  val ACTOR_NAME = "ingestKafkaProducerSupervisorActor"

  def getActorPath:String = "/user/"+ACTOR_NAME

  def props: Props = Props[IngestKafkaProducerSupervisorActor]
}
