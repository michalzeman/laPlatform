package com.la.platform.ingest.rest

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern._
import com.la.platform.ingest.actors.KafkaIngestProducerActor.{DataIngested, IngestData => ProduceData}
import com.la.platform.ingest.actors.KafkaIngestProducerActorSelection
import com.la.platform.rest.common.AbstractRestService

import scala.concurrent.Future

/**
  * Created by zemi on 25/10/2016.
  */
class IngestRestService(implicit system: ActorSystem) extends AbstractRestService with KafkaIngestProducerActorSelection {

  override def buildRoute(): Route =
    path("ingest") {
      post {
        entity(as[Ingest]) { entity => complete(ingestData(entity))}
      } ~
        get {
          complete("OK")
        }
    }

  def ingestData(ingestData: Ingest): Future[String] = {
    completeAndCleanUpAct({
      (select ? ProduceData(1, ingestData.data, ingestData.originator)).mapTo[DataIngested].map(response => "OK")
    })
  }

}
