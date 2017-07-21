package com.la.platform.ingest.rest

import java.util.UUID

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.la.platform.common.rest.AbstractRestService
import com.la.platform.ingest.bus.{IngestEventBus, IngestEventBusExtension}
import com.la.platform.ingest.actors.KafkaIngestProducerActor.IngestData

/**
  * Created by zemi on 25/10/2016.
  */
class IngestRestService(implicit val system: ActorSystem) extends AbstractRestService {

  val ingestEventBus: IngestEventBus = IngestEventBusExtension(system).eventBus

  override def buildRoute(): Route =
    path("ingest") {
      post {
        entity(as[Ingest]) { entity => complete(ingestData(entity))}
      } ~
        get {
          complete("OK")
        }
    }

  def ingestData(ingestData: Ingest): String = {
    system.eventStream
    ingestEventBus.publish(IngestData(UUID.randomUUID(), ingestData.data, ingestData.originator))
    "OK"
  }

}
