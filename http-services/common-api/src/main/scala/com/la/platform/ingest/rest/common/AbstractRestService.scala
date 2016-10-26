package com.la.platform.ingest.rest.common

import akka.actor.{ActorRef, ActorSystem, PoisonPill}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.util.Timeout
import spray.json.DefaultJsonProtocol

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}

/**
  * Created by zemi on 25/10/2016.
  */
abstract class AbstractRestService(implicit system: ActorSystem) extends RestEndpointRoute with DefaultJsonProtocol with SprayJsonSupport {

  protected implicit val timeout: Timeout = 5000 milliseconds

  protected implicit val systemAkka = system

  protected implicit val executorService = scala.concurrent.ExecutionContext.Implicits.global

  def stopActors(actors: ActorRef*): Unit = {
    actors.foreach(actor => actor ! PoisonPill)
  }

  def destroyActors(actors: ActorRef*): Unit = {
    actors.foreach(actor => actor ! PoisonPill)
  }

  /**
    * complete the action, resolve future result and stop actros
    * @param execute
    * @param actors
    * @tparam R
    * @return
    */
  def completeAndCleanUpAct[R](execute: => Future[R])(actors: ActorRef*): Future[R] = {
    execute.andThen {
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
    * @param execute
    * @tparam R
    * @return
    */
  def completeAndCleanUpAct[R](execute: => Future[R]): Future[R] = {
    execute.andThen {
      case Success(s) => {
        s
      }
      case Failure(e) => {
        e
      }
    }
  }
}
