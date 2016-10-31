package com.la.platform.batch.kafka

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

/**
  * Created by zemi on 31/10/2016.
  */
class KafkaProducerWrapper(createProducer:() => KafkaProducer[String, String]) extends Serializable {

  lazy val producer = createProducer()

  /**
    * Send message into the topic
    * @param topic - name of topic
    * @param key - key of message
    * @param value - value of message
    */
  def send(topic: String, key: String,value: String): Unit = {
    producer.send(new ProducerRecord[String, String](topic, key, value))
  }

}


object KafkaProducerWrapper {
  def apply(config: java.util.Map[String, java.lang.Object]): KafkaProducerWrapper = {
    val createProducer = () => {
      val producer = new KafkaProducer[String, String](config)

      sys.addShutdownHook {
        producer.close()
      }

      producer
    }
    new KafkaProducerWrapper(createProducer)
  }
}