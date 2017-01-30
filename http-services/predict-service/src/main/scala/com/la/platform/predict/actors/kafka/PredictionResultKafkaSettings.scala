package com.la.platform.predict.actors.kafka

import com.la.platform.common.settings.KafkaSettings
import com.typesafe.config.Config

/**
  * Created by zemi on 19/12/2016.
  */
class PredictionResultKafkaSettings(config: Config) extends KafkaSettings(config) {
  override def topic = config.getString("kafka.producer.prediction.topic")
}

object PredictionResultKafkaSettings {
  def apply(config: Config): PredictionResultKafkaSettings = new PredictionResultKafkaSettings(config)
}
