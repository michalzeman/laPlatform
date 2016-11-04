package com.la.platform.common.rest

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.headers.{`Access-Control-Allow-Credentials`, `Access-Control-Allow-Headers`, `Access-Control-Allow-Methods`, `Access-Control-Max-Age`}
import akka.http.scaladsl.model.{HttpHeader, HttpMethods, HttpResponse}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import spray.json.DefaultJsonProtocol

/**
  * Created by zemi on 18/08/16.
  */
trait RestEndpointRoute extends CorsSupport with DefaultJsonProtocol with SprayJsonSupport{

  def buildRoute(): Route;

  val myExceptionHandler = ExceptionHandler {
    case exc: Exception =>
      extractUri { uri =>
        println(s"Request to $uri could not be handled normally")
        complete(HttpResponse(InternalServerError, entity = exc.getMessage))
      }
  }

  def getRoute(): Route = {
    cors {
      handleExceptions(myExceptionHandler) {
        buildRoute()
      }
    }
  }

  override val corsAllowOrigins: List[String] = List("*")

  override val corsAllowedHeaders: List[String] = List("Origin", "X-Requested-With", "Content-Type", "Accept", "Accept-Encoding", "Accept-Language", "Host", "Referer", "User-Agent")

  override val corsAllowCredentials: Boolean = true

  override val optionsCorsHeaders: List[HttpHeader] = List[HttpHeader](
    `Access-Control-Allow-Methods`(HttpMethods.GET, HttpMethods.PUT, HttpMethods.POST, HttpMethods.DELETE),
    `Access-Control-Allow-Headers`(corsAllowedHeaders.mkString(", ")),
    `Access-Control-Max-Age`(60 * 60 * 24 * 20), // cache pre-flight response for 20 days
    `Access-Control-Allow-Credentials`(corsAllowCredentials)
  )

}
