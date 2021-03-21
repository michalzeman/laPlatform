package com.la.platform.batch.ingest


import com.la.platform.batch.cli.DataJobMain
import com.la.platform.batch.common.constants._
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Dataset, Encoders, SparkSession}
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * Created by zemi on 28/10/2016.
 */
object IngestDataJob extends DataJobMain[IngestDataCliParams] {

  def run(spark: SparkSession, opt: IngestDataCliParams): Unit = {
    import spark.implicits._

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
      .foreachRDD(rdd => processRecord(spark, rdd, generateFileName(workingDirectory)))

    streamingContext.start()
    streamingContext.awaitTermination()
  }

  def processRecord(spark: SparkSession, record: RDD[String], filePat: () => String): Unit = {
    val json: Dataset[String] = spark.createDataset(record)(Encoders.STRING)
    json.show()

    if (!json.isEmpty) {
      json.write.format("text").save(filePat())
    }
  }

  def consumerParams(opt: IngestDataCliParams): Map[String, Object] = {
    Map[String, Object](
      "bootstrap.servers" -> opt.getZkUrl,
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> appName,
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )
  }

  def generateFileName(workingDir: String): () => String = () => s"${workingDir}$INGEST_DATA_PREFIX_PATH${java.util.UUID.randomUUID.toString}-${System.currentTimeMillis}"

  override def appName: String = "IngestDataJob"

  override def getCliContext(args: Array[String]): IngestDataCliParams = IngestDataCliParams(args)
}
