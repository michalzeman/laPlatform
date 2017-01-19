package com.la.platform.ingest.actors

import akka.actor.Props
import akka.routing.FromConfig
import com.la.platform.common.actors.kafka.producer.{AbstractKafkaProducerActor, ProducerFactory}
import com.la.platform.common.settings.KafkaSettings
import com.la.platform.ingest.actors.KafkaIngestProducerActor.{DataIngested, IngestData}
import org.apache.kafka.clients.producer.{Callback, ProducerRecord, RecordMetadata}
import net.liftweb.json.Serialization.write

/**
  * Created by zemi on 25/10/2016.
  */
class KafkaIngestProducerActor(producerFactory: ProducerFactory[Int, String, KafkaSettings])
  extends AbstractKafkaProducerActor[IngestData, Int, String, KafkaSettings](producerFactory) {


  /**
    * Produce event into the kafka
    *
    * @param ingestData
    */
  override def sendMsgToKafka(ingestData: IngestData): Unit = {
    log.info(s"${getClass.getCanonicalName} produceData() ->")
    val now = java.time.LocalDateTime.now().format(producerFactory.settings.polish)
    val messageVal = write(KafkaIngestDataMessage(ingestData.value, ingestData.originator, now))
    log.debug(s"${getClass.getCanonicalName} produceData() -> message: $messageVal")
    val record = new ProducerRecord[Int, String](topic, ingestData.key, messageVal)
    producer.send(record, new Callback {
      override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit = {
        log.debug("produceData() -> finished")
        if (exception != null) {
          log.error(exception.getMessage, exception)
        }
      }
    })
    producer.flush()
    sender ! DataIngested(messageVal)
  }

}

object KafkaIngestProducerActor {

  case class IngestData(key: Int, value: String, originator: Option[String])

  case class DataIngested(value: String);

  val ACTOR_NAME = "kafkaIngestProducer"

  //  def props: Props = Props[KafkaIngestProducerActor]
  def props(producerFactory: KafkaIngestProducerFactory): Props = FromConfig.props(Props(classOf[KafkaIngestProducerActor], producerFactory))
}
