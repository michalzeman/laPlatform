package com.la.platform.batch.ingest


import com.la.platform.batch.cli.{DataJobMain}
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka._

/**
  * Created by zemi on 28/10/2016.
  */
object IngestDataJob extends DataJobMain[IngestDataCliParams] {

  def run(spark: SparkSession, opt:IngestDataCliParams): Unit = {

    val workingDirectory = opt.dataDir

    val ssc = new StreamingContext(spark.sparkContext, Seconds(2))
    ssc.checkpoint("checkpoint")

    val topicMap = Map("ingest-data" -> 1)
    val lines = KafkaUtils.createStream(ssc, opt.getZkUrl, "test", topicMap).filter(line => !line._2.isEmpty)

    lines.map(con => con._2).foreachRDD(rdd => {
      val spark = SparkSession.builder.config(rdd.sparkContext.getConf).getOrCreate()
      rdd.saveAsTextFile(workingDirectory + "/data/ingest")
      val json = spark.read.json(rdd)
      json.show()
    })

    ssc.start()
    ssc.awaitTermination()
  }

  override def appName: String = "IngestDataJob"

  override def getCliContext(args:Array[String]): IngestDataCliParams = IngestDataCliParams(args)
}
