package com.la.platform.predict.actors.kafka.streams

import akka.actor.{ActorRef, ActorSystem}

/**
  * Created by zemi on 31/03/2018.
  */
trait ConsumerReloadModelStreamBuilder {
  def build(system: ActorSystem, supervisor: ActorRef): ConsumerReloadModelStream
}

object ConsumerReloadModelStreamBuilder {
  def builder: ConsumerReloadModelStreamBuilder = new ConsumerReloadModelStreamBuilderImpl()
}

private class ConsumerReloadModelStreamBuilderImpl extends ConsumerReloadModelStreamBuilder {

  override def build(system: ActorSystem, supervisor: ActorRef): ConsumerReloadModelStream = {
    new ConsumerReloadModelStreamImpl(supervisor, system)
  }
}

