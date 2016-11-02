package com.la.platform.ingest.actors

import akka.actor.{Actor, ActorLogging, Props}
import akka.routing.FromConfig
import com.la.platform.ingest.common.util.KafkaIngestSettings
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord, RecordMetadata}
import net.liftweb.json._
import net.liftweb.json.Serialization.write

/**
  * Created by zemi on 25/10/2016.
  */
class KafkaIngestProducerActor extends Actor with ActorLogging {

  import KafkaIngestProducerActor._

  implicit val formats = DefaultFormats

  val polish = java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy H:mm:ss.SSS")

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
      val messageVal = write(KafkaIngestDataMessage(ingestData.value, ingestData.originator, now))
      log.debug(s"${getClass.getCanonicalName} produceData() -> message: $messageVal")
      val record = new ProducerRecord[Int, String](topic, ingestData.key, messageVal)
      producer.send(record)
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

//  def props: Props = Props[KafkaIngestProducerActor]
  def props: Props = FromConfig.props(Props[KafkaIngestProducerActor])
}
