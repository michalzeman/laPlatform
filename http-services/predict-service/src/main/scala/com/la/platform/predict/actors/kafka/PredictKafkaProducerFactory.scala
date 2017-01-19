package com.la.platform.predict.actors.kafka

import java.util.Properties

import com.la.platform.common.actors.kafka.producer.ProducerFactory
import com.typesafe.config.Config

/**
  * Created by zemi on 07/11/2016.
  */
class PredictKafkaProducerFactory(config: Config) extends ProducerFactory[Int, String, PredictKafkaSettings] {

  override protected def getSettings: PredictKafkaSettings = PredictKafkaSettings(config)
}

object PredictKafkaProducerFactory {
  def apply(config: Config): PredictKafkaProducerFactory = new PredictKafkaProducerFactory(config)
}
