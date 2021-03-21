package com.la.platform.predict.actors.kafka.streams

import akka.actor.{ActorRef, ActorSystem}
import akka.stream.ActorMaterializer

/**
 * Created by zemi on 03/04/2018.
 */
trait PredictionResultKafkaProducerStreamBuilder {
  def build(system: ActorSystem, supervisor: ActorRef)
           (implicit materializer: ActorMaterializer): PredictionResultKafkaProducerStream
}

object PredictionResultKafkaProducerStreamBuilder {
  def builder(): PredictionResultKafkaProducerStreamBuilder = new PredictionResultKafkaProducerStreamBuilderImpl()
}

private class PredictionResultKafkaProducerStreamBuilderImpl extends PredictionResultKafkaProducerStreamBuilder {
  def build(system: ActorSystem, supervisor: ActorRef)
           (implicit materializer: ActorMaterializer): PredictionResultKafkaProducerStream =
    new PredictionResultKafkaProducerStreamImpl(system, supervisor)
}