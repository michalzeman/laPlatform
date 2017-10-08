package com.la.platform.common.streams

import akka.NotUsed
import akka.actor.{ActorRef, ActorSystem}
import akka.event.{Logging, LoggingAdapter}
import akka.kafka.ProducerSettings
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import io.reactivex.processors.PublishProcessor
import org.apache.kafka.clients.producer.KafkaProducer

import scala.concurrent.ExecutionContextExecutor

/**
  * Created by zemi on 08/10/2017.
  */
abstract class AbstractKafkaProducerStream[D, K, V](system: ActorSystem, supervisor: ActorRef, implicit val materializer: ActorMaterializer) {
  protected val log: LoggingAdapter = Logging(system, getClass)

  protected implicit val executorService: ExecutionContextExecutor = system.dispatcher

  protected def getBootstrapServers: String

  def getProducerSettings: ProducerSettings[K, V]

  protected val producerSettings: ProducerSettings[K, V] = initProducerSettings

  protected def initProducerSettings: ProducerSettings[K, V] =
    getProducerSettings.withBootstrapServers(getBootstrapServers)

  protected val kafkaProducer: KafkaProducer[K, V] = producerSettings.createKafkaProducer()

  protected val publisher: PublishProcessor[D] = PublishProcessor.create[D]()

  protected val producerSource: Source[D, NotUsed] = Source.fromPublisher(publisher)
}
