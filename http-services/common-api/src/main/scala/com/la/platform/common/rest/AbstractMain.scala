package com.la.platform.common.rest

import java.lang.management.ManagementFactory

import akka.actor.ActorSystem
import akka.event.Logging
import akka.event.Logging._
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.stream.ActorMaterializer
import com.la.platform.common.settings.Settings
import com.typesafe.config.Config

import scala.annotation.tailrec
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.concurrent.duration._

/**
  * Created by zemi on 25/10/2016.
  */
trait AbstractMain extends App {

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer :ActorMaterializer = ActorMaterializer()
  implicit val ec: ExecutionContextExecutor = system.dispatcher

  lazy val settings = Settings(system.settings.config)

  val routes = logRequestResult("", InfoLevel)(buildRoutes())

  val bindingFuture: Future[ServerBinding] = Http().bindAndHandleAsync(Route.asyncHandler(routes), settings.Http.interface, settings.Http.port)

  bindingFuture recoverWith {
    case _ => system.terminate()
  }

  /**
    * Build all available routs
    * @return Route
    */
  def buildRoutes(): Route = {
    @tailrec
    def chainRoutes(routes: List[RestEndpointRoute], route: Route): Route = {
      routes match {
        case r::rl => chainRoutes(rl, r.getRoute ~ route)
        case Nil => route
      }
    }
    chainRoutes(initRestEndpoints, healthRoutes)
  }

  /**
    * create health route
    * @return Route
    */
  def healthRoutes = pathPrefix("health") {
    path("ping") {
      get {
        complete("OK")
      }
    } ~ path("uptime") {
      get {
        complete(getUptime.toString)
      }
    }
  }

  private def getUptime = Duration(ManagementFactory.getRuntimeMXBean.getUptime, MILLISECONDS).toSeconds

  /**
    * Init rest endpoints
    * @return
    */
  def initRestEndpoints: List[RestEndpointRoute]

}
