package com.la.platform.ingest.bus

import akka.actor.ActorRef
import akka.event.{ActorEventBus, EventBus, LookupClassification}
import com.la.platform.ingest.actors.KafkaIngestProducerActor.IngestData

/**
  * Created by zemi on 18/07/2017.
  */
class IngestEventBus extends EventBus with LookupClassification with ActorEventBus {
  override type Event = IngestData
  override type Classifier = Class[_]

  override protected def mapSize(): Int = 128

  override protected def classify(event: Event): Classifier = event.getClass

  override protected def publish(event: Event, subscriber: Subscriber): Unit = {
    subscriber ! event
  }
}
