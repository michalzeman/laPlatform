package com.la.platform.common.rest

import akka.actor.ActorSystem
import akka.event.Logging._
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.stream.ActorMaterializer
import com.la.platform.common.settings.Settings

import java.lang.management.ManagementFactory
import scala.annotation.tailrec
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

/**
 * Created by zemi on 25/10/2016.
 */
trait AbstractMain extends App {

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val ec: ExecutionContextExecutor = system.dispatcher

  lazy val settings = Settings(system.settings.config)

  val routes = logRequestResult("", InfoLevel)(buildRoutes())

  val bindingFuture: Future[ServerBinding] = Http().bindAndHandleAsync(
    Route.asyncHandler(routes),
    settings.Http.interface,
    settings.Http.port
  )

  bindingFuture onComplete {
    case Success(binding) => system.log.info(s"Server started on port ${binding.localAddress.getPort}")
    case Failure(ex) => system.log.error(s"Failed to bind to ${settings.Http.interface}:${settings.Http.port}", ex)
  }

  bindingFuture recoverWith {
    case _ => system.terminate()
  }

  /**
   * Build all available routs
   *
   * @return Route
   */
  def buildRoutes(): Route = {
    @tailrec
    def chainRoutes(routes: List[RestEndpointRoute], route: Route): Route = {
      routes match {
        case r :: rl => chainRoutes(rl, r.getRoute ~ route)
        case Nil => route
      }
    }

    chainRoutes(initRestEndpoints, healthRoutes)
  }

  /**
   * create health route
   *
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
   *
   * @return
   */
  def initRestEndpoints: List[RestEndpointRoute]

}
