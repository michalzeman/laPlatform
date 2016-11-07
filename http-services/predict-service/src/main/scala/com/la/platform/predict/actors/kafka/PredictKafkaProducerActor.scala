package com.la.platform.predict.actors.kafka


import akka.actor.Props
import com.la.platform.common.actors.kafka.producer.{AbstractKafkaProducerActor, ProducerFactory}
import org.apache.kafka.clients.producer.{Callback, ProducerRecord, RecordMetadata}
import net.liftweb.json.Serialization.write

/**
  * Created by zemi on 03/11/2016.
  */
class PredictKafkaProducerActor(producerFactory: ProducerFactory[Int, String])
  extends AbstractKafkaProducerActor[PredictRequestMsg, Int, String](producerFactory) {

  /**
    * Send prediction message to kafka cluster
    * @param msg - message to send
    */
  def sendMsgToKafka(msg: PredictRequestMsg): Unit = {
    val senderPath = sender().path.toString
    val kafkaMsg = write(PredictionJsonMsg(msg.data, senderPath))
    log.debug(s"${getClass.getCanonicalName} produceData() -> message: $kafkaMsg")
    val record = new ProducerRecord[Int, String](topic, 1, kafkaMsg)
    producer.send(record, new Callback {
      override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit = {
        log.debug("produceData() -> finished")
        if (exception != null) {
          log.error(exception.getMessage, exception)
        }
      }
    })
    producer.flush()
    sender ! PredictRequestMsgSent
  }

}

object PredictKafkaProducerActor {

  val actor_name = "PredictKafkaProducer"

  def props(producerFactory: PredictKafkaProducerFactory): Props = Props(classOf[PredictKafkaProducerActor], producerFactory)
}