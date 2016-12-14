package com.la.platform.speed.data.rest

import akka.actor.ActorSystem
import akka.http.scaladsl.model.ws.TextMessage
import akka.http.scaladsl.model.ws.TextMessage.Strict
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.{Flow, Sink}
import com.la.platform.common.rest.AbstractRestService
import akka.http.scaladsl.server.Directives._
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.ActorMaterializer
import akka.kafka.scaladsl.Consumer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer

/**
  * Created by zemi on 02/12/2016.
  */
class SpeedDataRestService(implicit system: ActorSystem) extends AbstractRestService {

  implicit val materializer = ActorMaterializer()

//  val producerRef = system.actorOf(Props[SourcePublisherActor])
//
//  val publisher = ActorPublisher[String](producerRef)

  val consumerSettings = ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
    .withBootstrapServers("localhost:9092")
    .withGroupId("PredictSpeedData")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")

  val source = Consumer.committableSource(consumerSettings, Subscriptions.topics("prediction-data"))
    .map(msg => msg.record.value())



  override def buildRoute(): Route = path("speeddata") {
    handleWebSocketMessages(speedData)
  }

  def speedData: Flow[Any, Strict, Any] = Flow.fromSinkAndSource(Sink.ignore, source map { data => {
    TextMessage.Strict(data)
  }})
}
