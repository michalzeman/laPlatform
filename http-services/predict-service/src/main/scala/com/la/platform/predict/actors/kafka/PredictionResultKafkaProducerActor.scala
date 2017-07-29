package com.la.platform.predict.actors.kafka


import akka.Done
import akka.actor.{Actor, ActorLogging, Props}
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import com.la.platform.predict.actors.ml.PredictServiceActor
import io.reactivex.processors.PublishProcessor
import net.liftweb.json.DefaultFormats
import net.liftweb.json.Serialization.write
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.{IntegerSerializer, StringSerializer}

import scala.concurrent.Future

/**
  * Created by zemi on 03/11/2016.
  */
class PredictionResultKafkaProducerActor extends Actor with ActorLogging {

  implicit val formats = DefaultFormats

  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val bootstrap_servers: String = context.system.settings.config.getString("kafka.producer.bootstrap.servers")

  val topic: String = context.system.settings.config.getString("kafka.producer.prediction.topic")

  val producerSettings: ProducerSettings[Integer, String] = ProducerSettings(context.system, new IntegerSerializer, new StringSerializer)
    .withBootstrapServers(bootstrap_servers)

  val kafkaProducer: KafkaProducer[Integer, String] = producerSettings.createKafkaProducer()

  val publisher: PublishProcessor[PredictServiceActor.PredictionResult] = PublishProcessor.create()

  //  val subject: PublishSubject[PredictServiceActor.PredictionResult] = PublishSubject.create[PredictServiceActor.PredictionResult]()

  val producerSource: Future[Done] = Source.fromPublisher(publisher)
    .map(msg => {
      val kafkaMsg = write(msg)
      log.debug(s"${getClass.getCanonicalName} produceData() -> message: $kafkaMsg")
      new ProducerRecord[Integer, String](topic, 1, kafkaMsg)
    })
    .runWith(Producer.plainSink(producerSettings, kafkaProducer))

  override def receive: Receive = {
    case msg: PredictServiceActor.PredictionResult => sendMsgToKafka(msg)
    case _ => log.warning("problem !!!!!!!")
  }

  /**
    * Send prediction message to kafka cluster
    *
    * @param msg - message to send
    */
  def sendMsgToKafka(msg: PredictServiceActor.PredictionResult): Unit = {
    log.info(s"${getClass.getCanonicalName} produceData() -> message: $msg")
    publisher.onNext(msg)
    sender ! PredictRequestMsgSent
  }

}

object PredictionResultKafkaProducerActor {

  val actor_name = "PredictionResultKafkaProducer"

  def props: Props = Props(classOf[PredictionResultKafkaProducerActor])
}