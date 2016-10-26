package com.la.platform.ingest.actors

import akka.actor.{Actor, ActorLogging, Props}
import com.la.platform.ingest.common.util.KafkaIngestSettings
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord, RecordMetadata}

/**
  * Created by zemi on 25/10/2016.
  */
class KafkaIngestProducerActor extends Actor with ActorLogging {

  import KafkaIngestProducerActor._

  val polish = java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy H:mm:ss")

  val settings = KafkaIngestSettings(context.system.settings.config)

  val topic = settings.ingest_topic

  val producer = new KafkaProducer[Int, String](settings.getKafkaProps)


  override def receive: Receive = {
    case data:IngestData => produceData(data)
  }

  /**
    * Produce event into the kafka
    * @param ingestData
    */
  private def produceData(ingestData: IngestData): Unit = {
    log.info(s"${getClass.getCanonicalName} produceData() ->")
      val now = java.time.LocalDateTime.now().format(polish)
      val originator = ingestData.originator match {
        case Some(org) => org
        case None => ""
      }
      val messageVal =
        s"""{
           "message": "${ingestData.value}",
           "originator": "$originator",
           "time": "${now}"
          }"""
      log.debug(s"${getClass.getCanonicalName} produceData() -> message: $messageVal")
      val record = new ProducerRecord[Int, String](topic, ingestData.key, messageVal)
      producer.send(record).get()
      sender ! DataIngested(messageVal)
  }

  @scala.throws[Exception](classOf[Exception])
  override def postStop(): Unit = {
    log.info(s"${getClass.getCanonicalName} postStop -> going to close producer")
    producer.close()
    super.postStop()
  }
}

object KafkaIngestProducerActor {

  case class IngestData(key: Int, value: String, originator: Option[String])

  case class DataIngested(value: String);

  val ACTOR_NAME = "kafkaIngestProducer"

  def props: Props = Props[KafkaIngestProducerActor]
}
