package com.la.platform.predict.actors.kafka


import akka.actor.Props
import com.la.platform.common.actors.kafka.producer.AbstractKafkaProducerActor
import com.la.platform.common.settings.KafkaSettings
import com.la.platform.predict.actors.ml.PredictServiceActor
import org.apache.kafka.clients.producer.{Callback, ProducerRecord, RecordMetadata}
import net.liftweb.json.Serialization.write

/**
  * Created by zemi on 03/11/2016.
  */
class PredictionResultKafkaProducerActor(producerFactory: PredictionResultKafkaProducerFactory)
  extends AbstractKafkaProducerActor[PredictServiceActor.PredictionResult, Int, String, PredictionResultKafkaSettings](producerFactory) {

  /**
    * Send prediction message to kafka cluster
    * @param msg - message to send
    */
  def sendMsgToKafka(msg: PredictServiceActor.PredictionResult): Unit = {
    val kafkaMsg = write(msg)
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

object PredictionResultKafkaProducerActor {

  val actor_name = "PredictionResultKafkaProducer"

  def props(producerFactory: PredictionResultKafkaProducerFactory): Props = Props(classOf[PredictionResultKafkaProducerActor], producerFactory)
}