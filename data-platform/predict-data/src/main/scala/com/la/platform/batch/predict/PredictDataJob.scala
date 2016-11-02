package com.la.platform.batch.predict

import java.util
import java.util.UUID

import com.la.platform.batch.cli.DataJobMain
import com.la.platform.batch.kafka.KafkaProducerWrapper
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Created by zemi on 31/10/2016.
  */
object PredictDataJob extends DataJobMain[PredictDataParams] {

  override def appName: String = "PredictData"

  override def run(spark: SparkSession, opt: PredictDataParams): Unit = {

    val kafkaProducer = spark.sparkContext.broadcast(KafkaProducerWrapper(getKafkaProps(opt)))

    val ssc = new StreamingContext(spark.sparkContext, Seconds(1))

    ssc.checkpoint("checkpoint")

    val inTopicMap = Map("predict" -> 1)
    val lines = KafkaUtils.createStream(ssc, opt.getZkUrl, "PredictDataProducer", inTopicMap).filter(line => !line._2.isEmpty)

    lines.map(con => con._2).foreachRDD(rdd => {
      rdd.foreachPartition(rddPart => {
        rddPart.foreach(rdd => {
          kafkaProducer.value.send("prediction-result", rdd,"Zemo hi!!! from => "+rdd)
        })
      })
    })

    ssc.start()
    ssc.awaitTermination()
  }

  def getKafkaProps(opt: PredictDataParams): java.util.Map[String, Object] = {
    val props = new util.HashMap[String, Object]()
    props.put("bootstrap.servers", opt.getZkProducer)
    val guid = UUID.randomUUID().toString;
    props.put("client.id", appName + guid)
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props
  }

  override def getCliContext(args: Array[String]): PredictDataParams = PredictDataParams(args)
}
