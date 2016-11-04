package com.la.platform.batch.predict

import java.util
import java.util.UUID

import com.la.platform.batch.cli.DataJobMain
import com.la.platform.batch.kafka.KafkaProducerWrapper
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.kafka010.ConsumerStrategies._
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies._
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Created by zemi on 31/10/2016.
  */
object PredictDataJob extends DataJobMain[PredictDataParams] {

  override def appName: String = "PredictData"

  override def run(spark: SparkSession, opt: PredictDataParams): Unit = {

    val kafkaProducer = spark.sparkContext.broadcast(KafkaProducerWrapper(getKafkaProps(opt)))

    val streamingContext = new StreamingContext(spark.sparkContext, Seconds(10))

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "localhost:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> appName,
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    val topics = Array("prediction")
    val stream = KafkaUtils.createDirectStream[String, String](
      streamingContext,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )

    stream.map(record => record.value)
      .filter(record => !record.isEmpty)
      .foreachRDD(
        rdd => {
          val spark = SparkSession.builder.config(rdd.sparkContext.getConf).getOrCreate()

          rdd.foreachPartition(rddPart => {
            rddPart.foreach(rdd => {
              kafkaProducer.value.send("prediction-result", rdd, "Zemo hi!!! from => " + rdd)
            })
          })
        }
      )

    streamingContext.start()
    streamingContext.awaitTermination()
  }

  def getKafkaProps(opt: PredictDataParams): java.util.Map[String, Object] = {
    val props = new util.HashMap[String, Object]()
    props.put("bootstrap.servers", opt.getZkProducer)
    val guid = UUID.randomUUID().toString
    props.put("client.id", appName + guid)
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props
  }

  override def getCliContext(args: Array[String]): PredictDataParams = PredictDataParams(args)
}
