package com.la.platform.predict.actors.kafka

import java.util.Properties

import com.la.platform.common.actors.kafka.producer.ProducerFactory
import com.typesafe.config.Config

/**
  * Created by zemi on 07/11/2016.
  */
class PredictionResultKafkaProducerFactory(config: Config) extends ProducerFactory[Int, String, PredictionResultKafkaSettings] {

  override protected def getSettings: PredictionResultKafkaSettings = PredictionResultKafkaSettings(config)
}

object PredictionResultKafkaProducerFactory {
  def apply(config: Config): PredictionResultKafkaProducerFactory = new PredictionResultKafkaProducerFactory(config)
}
