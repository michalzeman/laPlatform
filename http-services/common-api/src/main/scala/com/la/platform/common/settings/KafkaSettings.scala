package com.la.platform.common.settings

import java.util.UUID

import com.typesafe.config.Config

/**
  * Created by zemi on 26/10/2016.
  */
class KafkaSettings(config: Config) {

  private val bootstrap_servers_key: String = "kafka.producer.bootstrap.servers"
  private val client_id_key: String = "kafka.producer.client.id"
  private val key_serializer_key: String = "kafka.producer.key.serializer"
  private val value_serializer_key: String = "kafka.producer.value.serializer"

  val bootstrap_servers = config.getString(bootstrap_servers_key)
  val client_id = config.getString(client_id_key)
  val key_serializer = config.getString(key_serializer_key)
  val value_serializer = config.getString(value_serializer_key)
  def topic = config.getString("kafka.producer.topic")

  val polish = java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy H:mm:ss.SSS")

  def getKafkaProducerProps(): java.util.Properties = {
    val props = new java.util.Properties()
    props.put("bootstrap.servers", bootstrap_servers)
    props.put("client.id", client_id+UUID.randomUUID().toString)
    props.put("key.serializer", key_serializer)
    props.put("value.serializer", value_serializer)
    props
  }

}

object KafkaSettings {
  def apply(config: Config): KafkaSettings = new KafkaSettings(config)
}
