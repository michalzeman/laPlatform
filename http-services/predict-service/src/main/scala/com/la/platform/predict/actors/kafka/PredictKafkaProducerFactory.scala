package com.la.platform.predict.actors.kafka

import java.util.Properties

import com.la.platform.common.actors.kafka.producer.ProducerFactory
import com.la.platform.common.settings.KafkaSettings
import com.typesafe.config.Config

/**
  * Created by zemi on 07/11/2016.
  */
class PredictKafkaProducerFactory(config: Config) extends ProducerFactory[Int, String] {

  val settings = KafkaSettings(config)

  override def getSetting: Properties = settings.getKafkaProducerProps()

  override def getTopic: String = settings.ingest_topic
}

object PredictKafkaProducerFactory {
  def apply(config: Config): PredictKafkaProducerFactory = new PredictKafkaProducerFactory(config)
}
