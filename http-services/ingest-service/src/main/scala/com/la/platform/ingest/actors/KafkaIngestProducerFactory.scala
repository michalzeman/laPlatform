package com.la.platform.ingest.actors

import java.util.Properties

import com.la.platform.common.actors.kafka.producer.ProducerFactory
import com.la.platform.common.settings.KafkaSettings
import com.typesafe.config.Config

/**
  * Created by zemi on 07/11/2016.
  */
class KafkaIngestProducerFactory(config: Config) extends ProducerFactory[Int, String] {

  val settings = KafkaSettings(config)

  override def getSetting: Properties = settings.getKafkaProducerProps()

  override def getTopic: String = settings.topic
}

object KafkaIngestProducerFactory {
  def apply(config: Config): KafkaIngestProducerFactory = new KafkaIngestProducerFactory(config)
}
