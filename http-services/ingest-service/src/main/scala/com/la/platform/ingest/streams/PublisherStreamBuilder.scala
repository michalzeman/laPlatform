package com.la.platform.ingest.streams

import akka.actor.{ActorRef, ActorSystem}
import akka.stream.ActorMaterializer

/**
  * Created by zemi on 28/09/2017.
  */
trait PublisherStreamBuilder {
  def build(materializer: ActorMaterializer, system: ActorSystem, supervisor: ActorRef): PublisherStream
}

object PublisherStreamBuilder {
  def builder: PublisherStreamBuilder = new PublisherStreamBuilderImpl()
}

private class PublisherStreamBuilderImpl extends PublisherStreamBuilder {
  override def build(materializer: ActorMaterializer, system: ActorSystem, supervisor: ActorRef): PublisherStream =
    PublisherStreamImpl(materializer, system, supervisor)
}
