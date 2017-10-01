package com.la.platform.ingest.streams

import akka.actor.{ActorRef, ActorSystem}
import akka.stream.ActorMaterializer

/**
  * Created by zemi on 28/09/2017.
  */
trait PublisherStreamBuilder {
  def create(materializer: ActorMaterializer, system: ActorSystem, supervisor: ActorRef): PublisherStream
}

object PublisherStreamBuilder {
  def get: PublisherStreamBuilder = new PublisherStreamBuilderImpl()
}

private class PublisherStreamBuilderImpl extends PublisherStreamBuilder {
  override def create(materializer: ActorMaterializer, system: ActorSystem, supervisor: ActorRef): PublisherStream =
    PublisherStream(materializer, system, supervisor)
}
