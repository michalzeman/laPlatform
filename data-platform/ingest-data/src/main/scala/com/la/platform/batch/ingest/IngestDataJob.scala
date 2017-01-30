package com.la.platform.batch.ingest


import java.lang.Boolean

import com.la.platform.batch.cli.DataJobMain
import com.la.platform.batch.common.constants._

import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkContext
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

    val topics = Array("IngestData")
    val stream = KafkaUtils.createDirectStream[String, String](
      streamingContext,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )

    stream
      .map(record => record.value)
      .foreachRDD(
        rdd => {
          val spark = SparkSession.builder.config(rdd.sparkContext.getConf).getOrCreate()
          rdd.map(strVal => {
            val scMap = SparkContext.getOrCreate()
            scMap.parallelize(Seq(strVal))
          }).foreach(rddFiltered =>
            rddFiltered.saveAsTextFile(workingDirectory + s"${INGEST_DATA_PREFIX_PATH}${java.util.UUID.randomUUID.toString}-${System.currentTimeMillis}")
          )
          val json = spark.read.json(rdd)
          json.show()
        })
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
