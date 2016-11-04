package com.la.platform.batch.ingest


import java.lang.Boolean

import com.la.platform.batch.cli.DataJobMain
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe

/**
  * Created by zemi on 28/10/2016.
  */
object IngestDataJob extends DataJobMain[IngestDataCliParams] {

  def run(spark: SparkSession, opt: IngestDataCliParams): Unit = {

    val workingDirectory = opt.dataDir

    val streamingContext = new StreamingContext(spark.sparkContext, Seconds(10))
//    streamingContext.checkpoint("checkpoint")

    val kafkaParams = consumerParams(opt)

    val topics = Array("ingest-data")
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
        rdd.saveAsTextFile(workingDirectory + "/data/ingest")
        val json = spark.read.json(rdd)
        json.show()
      })
    //    val lines = KafkaUtils.createDirectStream(ssc, opt.getZkUrl, "test", topicMap).filter(line => !line._2.isEmpty)
    //
    //    lines.map(con => con._2).foreachRDD(rdd => {
    //      val spark = SparkSession.builder.config(rdd.sparkContext.getConf).getOrCreate()
    //      rdd.saveAsTextFile(workingDirectory + "/data/ingest")
    //      val json = spark.read.json(rdd)
    //      json.show()
    //    })

    streamingContext.start()
    streamingContext.awaitTermination()
  }

  def consumerParams(opt: IngestDataCliParams): Map[String, Object] = {
    Map[String, Object](
      "bootstrap.servers" -> opt.getZkUrl,
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> appName,
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: Boolean)
    )
  }

  override def appName: String = "IngestDataJob"

  override def getCliContext(args: Array[String]): IngestDataCliParams = IngestDataCliParams(args)
}
