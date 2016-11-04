package com.la.platform.predict.actors.kafka

import java.util.UUID

import akka.actor.{Actor, ActorLogging, Props}
import com.la.platform.common.settings.KafkaSettings
import org.apache.kafka.clients.producer.{Callback, KafkaProducer, ProducerRecord, RecordMetadata}
import net.liftweb.json._
import net.liftweb.json.Serialization.write

/**
  * Created by zemi on 03/11/2016.
  */
class PredictKafkaProducerActor extends Actor with ActorLogging {

  implicit val formats = DefaultFormats

  val settings = KafkaSettings(context.system.settings.config)

  val topic = settings.ingest_topic

  val producer = new KafkaProducer[Int, String](settings.getKafkaProducerProps)

  override def receive: Receive = {
    case msg:PredictKafkaProducerMsg => sendPredictMsgToKafka(msg)
    case _ => log.warning("problem !!!!!!!")
  }

  /**
    * Send prediction message to kafka cluster
    * @param msg - message to send
    */
  def sendPredictMsgToKafka(msg: PredictKafkaProducerMsg): Unit = {
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
  }

  @scala.throws[Exception](classOf[Exception])
  override def postStop(): Unit = {
    producer.close()
    super.postStop()
  }
}

object PredictKafkaProducerActor {

  val actor_name = "PredictKafkaProducer"

  def props: Props = Props[PredictKafkaProducerActor]
}