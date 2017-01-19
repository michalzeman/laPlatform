package com.la.platform.common.actors.kafka.producer

import java.util.Properties

import com.la.platform.common.settings.KafkaSettings
import org.apache.kafka.clients.producer.KafkaProducer

/**
  * Created by zemi on 07/11/2016.
  */
trait ProducerFactory[K, V, S <: KafkaSettings] {

  val settings = getSettings

  def getProducer: KafkaProducer[K, V] = {
    new KafkaProducer[K, V](getProperties)
  }

  def getProperties: Properties = settings.getKafkaProducerProps()

  def getTopic: String = settings.topic

  protected def getSettings: S
}
