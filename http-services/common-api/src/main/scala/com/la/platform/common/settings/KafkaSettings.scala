package com.la.platform.common.settings

import java.util.UUID

import com.typesafe.config.Config

/**
  * Created by zemi on 26/10/2016.
  */
class KafkaSettings(config: Config) {

  private val bootstrap_servers_key: String = "kafka.bootstrap.servers"
  private val client_id_key: String = "kafka.client.id"
  private val key_serializer_key: String = "kafka.key.serializer"
  private val value_serializer_key: String = "kafka.value.serializer"

  val bootstrap_servers = config.getString(bootstrap_servers_key)
  val client_id = config.getString(client_id_key)
  val key_serializer = config.getString(key_serializer_key)
  val value_serializer = config.getString(value_serializer_key)
  val ingest_topic = config.getString("kafka.ingest.topic")

  val polish = java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy H:mm:ss.SSS")

  def getKafkaProps(): java.util.Properties = {
    val props = new java.util.Properties()
    props.put("bootstrap.servers", bootstrap_servers)
    val guid = UUID.randomUUID().toString;
    props.put("client.id", client_id+guid)
    props.put("key.serializer", key_serializer)
    props.put("value.serializer", value_serializer)
    props
  }

}

object KafkaSettings {
  def apply(config: Config): KafkaSettings = new KafkaSettings(config)
}
