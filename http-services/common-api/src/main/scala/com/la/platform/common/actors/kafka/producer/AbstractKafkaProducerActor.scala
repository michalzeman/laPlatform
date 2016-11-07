package com.la.platform.common.actors.kafka.producer

import akka.actor.{Actor, ActorLogging}
import com.la.platform.common.settings.KafkaSettings
import net.liftweb.json.DefaultFormats
import org.apache.kafka.clients.producer.KafkaProducer

/**
  * Created by zemi on 07/11/2016.
  */
abstract class AbstractKafkaProducerActor[M, K, V](producerFactory: ProducerFactory[K, V]) extends Actor with ActorLogging {

  implicit val formats = DefaultFormats

  val settings = KafkaSettings(context.system.settings.config)

  val topic = settings.ingest_topic

  val producer = new KafkaProducer[Int, String](settings.getKafkaProducerProps)

  override def receive: Receive = {
    case msg:M => sendMsgToKafka(msg)
    case _ => log.warning("problem !!!!!!!")
  }

  def sendMsgToKafka(msg: M): Unit

  @scala.throws[Exception](classOf[Exception])
  override def postStop(): Unit = {
    producer.close()
    super.postStop()
  }
}
