package com.la.platform.ingest.actors

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.la.platform.ingest.actors.IngestActionActor.IngestResponse
import com.la.platform.ingest.actors.KafkaIngestProducerActor.{DataIngested, IngestData}
import com.la.platform.ingest.bus.{IngestEventBus, IngestEventBusExtension}
import com.la.platform.ingest.rest.Ingest

/**
  * Created by zemi on 20/08/2017.
  */
class IngestActionActor extends Actor with ActorLogging {

  val ingestEventBus: IngestEventBus = IngestEventBusExtension(context.system).eventBus

  override def receive :Receive = {
    case request: Ingest => {
      log.info(s"${self.path} -> IngestRequest")
      ingestEventBus.publish(IngestData(UUID.randomUUID(), request.data, request.originator, self))
      context.become(waitOnResponse(sender))
    }
  }

  private def waitOnResponse(requester: ActorRef) :Receive = {
    case DataIngested(m) => {
      log.debug(s"${self.path} -> DataIngested")
      requester ! IngestResponse(m)
      context.stop(self)
    }
  }

}

object IngestActionActor {

  case class IngestResponse(msg: String)

  def props: Props = Props[IngestActionActor]
}
