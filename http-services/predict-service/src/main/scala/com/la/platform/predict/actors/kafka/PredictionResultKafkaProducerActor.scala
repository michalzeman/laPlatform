package com.la.platform.predict.actors.kafka


import akka.actor.{Actor, ActorLogging, Props}
import akka.stream.ActorMaterializer
import com.la.platform.predict.actors.kafka.streams.{PredictionResultKafkaProducerStream, PredictionResultKafkaProducerStreamBuilder}
import com.la.platform.predict.actors.ml.PredictServiceActor

/**
  * Created by zemi on 03/11/2016.
  */
class PredictionResultKafkaProducerActor(predictionResultKafkaProducerStreamBuilder: PredictionResultKafkaProducerStreamBuilder)
  extends Actor with ActorLogging {

  implicit val materializer: ActorMaterializer = ActorMaterializer()

  var predictionResultKafkaProducerStream: Option[PredictionResultKafkaProducerStream] = None

  override def preStart(): Unit = {
    super.preStart()
    predictionResultKafkaProducerStream = Some(predictionResultKafkaProducerStreamBuilder.build(context.system, self))
  }

  override def postStop(): Unit = super.postStop()

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
    predictionResultKafkaProducerStream.foreach(_.onNext(msg))
    sender ! PredictRequestMsgSent
  }

}

object PredictionResultKafkaProducerActor {

  val actor_name = "PredictionResultKafkaProducer"

  def props(predictionResultKafkaProducerStreamBuilder: PredictionResultKafkaProducerStreamBuilder): Props =
    Props(new PredictionResultKafkaProducerActor(predictionResultKafkaProducerStreamBuilder))
}