package com.la.platform.common.rest

import akka.actor.{ActorRef, ActorSystem, PoisonPill}
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.util.Timeout
import spray.json.DefaultJsonProtocol

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

/**
  * Created by zemi on 25/10/2016.
  */
abstract class AbstractRestService(implicit system: ActorSystem) extends RestEndpointRoute with DefaultJsonProtocol with SprayJsonSupport {

  protected implicit val timeout: Timeout = 5000 milliseconds

  protected implicit val systemAkka: ActorSystem = system

  protected implicit val executorService: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  protected val log: LoggingAdapter = Logging(system, getClass)

  def stopActors(actors: ActorRef*): Unit = {
    actors.foreach(actor => actor ! PoisonPill)
  }

  def destroyActors(actors: ActorRef*): Unit = {
    actors.foreach(actor => actor ! PoisonPill)
  }

  /**
    * complete the action, resolve future result and stop actros
    *
    * @param execute
    * @param actors
    * @tparam R
    * @return
    */
  def completeAndCleanUpAct[R](execute:() => Future[R])(actors: ActorRef*): Future[R] = {
    execute().andThen {
      case Success(s) => {
        actors.foreach(actor => actor ! PoisonPill)
        s
      }
      case Failure(e) => {
        actors.foreach(actor => actor ! PoisonPill)
        e
      }
    }
  }

  /**
    * complete the action, resolve future result
    *
    * @param execute
    * @tparam R
    * @return
    */
  def completeAction[R](execute: => Future[R]): Future[R] = {
    execute.andThen {
      case Success(s) => {
        s
      }
      case Failure(e) => {
        e
      }
    }
  }

  def test(test:() => String): Unit = println(test.apply())
}
