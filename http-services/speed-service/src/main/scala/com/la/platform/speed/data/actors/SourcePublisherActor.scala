package com.la.platform.speed.data.actors

import java.math.BigInteger

import akka.actor.Actor.Receive
import akka.actor.ActorLogging
import akka.stream.actor.ActorPublisher
import akka.stream.actor.ActorPublisherMessage.{Cancel, Request}

/**
  * Created by zemi on 02/12/2016.
  */
class SourcePublisherActor extends ActorPublisher[String] with ActorLogging {

  var prev = BigInteger.ZERO
  var curr = BigInteger.ZERO

  def receive = {
    case Request(cnt) =>
      log.debug("[SourcePublisherActor] Received Request ({}) from Subscriber", cnt)
      sendFibs()
    case Cancel =>
      log.info("[SourcePublisherActor] Cancel Message Received -- Stopping")
      context.stop(self)
    case _ =>
  }

  def sendFibs() {
    while(isActive && totalDemand > 0) {
      onNext(nextFib())
    }
  }

  def nextFib(): String = {
    if(curr == BigInteger.ZERO) {
      curr = BigInteger.ONE
    } else {
      val tmp = prev.add(curr)
      prev = curr
      curr = tmp
    }
    curr.toString
  }

}
