package com.la.platform.common.actors.kafka.producer

import org.apache.kafka.clients.producer.KafkaProducer

/**
  * Created by zemi on 07/11/2016.
  */
trait ProducerFactory[K, V] {

  def getProducer: KafkaProducer[K, V] = {
    new KafkaProducer[K, V](getSetting)
  }

  def getSetting: java.util.Properties

  def getTopic: String
}
