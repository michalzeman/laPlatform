package com.la.platform.ingest.rest

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern._
import com.la.platform.common.rest.AbstractRestService
import com.la.platform.ingest.actors.IngestActionActor
import com.la.platform.ingest.bus.{IngestEventBus, IngestEventBusExtension}

import scala.concurrent.Future

/**
  * Created by zemi on 25/10/2016.
  */
class IngestRestService(implicit val system: ActorSystem) extends AbstractRestService {

  val ingestEventBus: IngestEventBus = IngestEventBusExtension(system).eventBus

  override def buildRoute(): Route =
    path("ingest") {
      post {
        entity(as[Ingest]) { entity => complete(ingestData(entity)) }
      } ~
        get {
          complete("OK")
        }
    }

  def ingestData(ingestData: Ingest): Future[String] = {
    getActionActor.flatMap(actionActor => completeAndCleanUpAct(() => {
      log.info("")
      (actionActor ? ingestData).mapTo[IngestActionActor.IngestResponse].map(respons => {
        log.debug("Ingest data done!")
        respons.msg
      })
    })(actionActor))
  }

  private def getActionActor: Future[ActorRef] = Future {
    system.actorOf(IngestActionActor.props)
  }

}
