package com.la.platform.ingest.actors.supervisors

import akka.actor.{ActorRef, Props}
import com.la.platform.common.actors.kafka.KafkaSupervisorActor
import com.la.platform.ingest.actors.KafkaIngestProducerActor
import com.la.platform.ingest.actors.KafkaIngestProducerActor.IngestData
import com.la.platform.ingest.bus.IngestEventBusExtension
import com.la.platform.ingest.streams.PublisherStreamBuilder

/**
  * Created by zemi on 26/10/2016.
  */
class IngestKafkaProducerSupervisorActor extends KafkaSupervisorActor {

  val kafkaIngestProducerActor: ActorRef = context.actorOf(KafkaIngestProducerActor.props(PublisherStreamBuilder.get),
    KafkaIngestProducerActor.ACTOR_NAME)

  context.watch(kafkaIngestProducerActor)
}

object IngestKafkaProducerSupervisorActor {

  val ACTOR_NAME = "ingestKafkaProducerSupervisorActor"

  def getActorPath:String = "/user/"+ACTOR_NAME

  def props: Props = Props(new IngestKafkaProducerSupervisorActor())
}
