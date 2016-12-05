package com.la.platform.batch.predict

import java.lang.Boolean
import java.util
import java.util.UUID

import com.la.platform.batch.cli.DataJobMain
import com.la.platform.batch.kafka.KafkaProducerWrapper
import com.la.platform.batch.ml.LogisticRegressionUtils._
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.ml.classification.LogisticRegressionModel
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.kafka010.ConsumerStrategies._
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies._
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Created by zemi on 31/10/2016.
  */
object PredictDataJob extends DataJobMain[PredictDataParams] {

  override def appName: String = {
    "PredictData" + UUID.randomUUID().toString
  }

  override def run(spark: SparkSession, opt: PredictDataParams): Unit = {

    val kafkaPredictionResultProducer = spark.sparkContext.broadcast(KafkaProducerWrapper(getKafkaProducerProp(opt)))

    val lrModel = spark.sparkContext.broadcast(LogisticRegressionModel.load(opt.dataDir+"mllib/lr/model"))

    val streamingContext = new StreamingContext(spark.sparkContext, Seconds(5))
//    val streamingContext = new StreamingContext(spark.sparkContext, Milliseconds(200))

    val kafkaParams = getKafkaConsumerProp(opt)

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
          spark.read.json(rdd).foreachPartition(rowIterator => rowIterator.foreach(row => {
            val predResult = predict(row, lrModel.value)
            val sender = row.getAs[String]("sender")
            val result =s"""{"data":"$predResult","sender":"$sender"}""".stripMargin
            kafkaPredictionResultProducer.value.send("prediction-result", sender, result)
            val data = row.getAs[String]("data")
            val speedData = s"""{"prediction":"$predResult","data":"$data"}""".stripMargin
            kafkaPredictionResultProducer.value.send("prediction-data", UUID.randomUUID().toString, speedData)
          }))
        }
      )

    streamingContext.start()
    streamingContext.awaitTermination()
  }

  def getKafkaConsumerProp(opt: PredictDataParams): Map[String, Object] = {
    Map[String, Object](
      "bootstrap.servers" -> opt.getZkUrl,
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> appName,
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: Boolean)
    )
  }

  def getKafkaProducerProp(opt: PredictDataParams): java.util.Map[String, Object] = {
    val props = new util.HashMap[String, Object]()
    props.put("bootstrap.servers", opt.getZkUrl)
    props.put("client.id", appName)
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props
  }

  override def getCliContext(args: Array[String]): PredictDataParams = PredictDataParams(args)
}
