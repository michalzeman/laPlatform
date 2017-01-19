package com.la.platform.predict.actors.kafka

import com.la.platform.common.settings.KafkaSettings
import com.typesafe.config.Config

/**
  * Created by zemi on 19/12/2016.
  */
class PredictKafkaSettings(config: Config) extends KafkaSettings(config) {
  override def topic = config.getString("kafka.producer.prediction.topic")
}

object PredictKafkaSettings {
  def apply(config: Config): PredictKafkaSettings = new PredictKafkaSettings(config)
}
