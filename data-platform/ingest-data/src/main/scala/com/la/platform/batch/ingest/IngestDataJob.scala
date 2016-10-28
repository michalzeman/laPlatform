package com.la.platform.batch.ingest

import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka._

/**
  * Created by zemi on 28/10/2016.
  */
object IngestDataJob {

  def main(args: Array[String]): Unit = {

    assert(args.length == 1)

    val workingDirectory = args(0)

    val spark = SparkSession
      .builder
      .appName("IngestDataJob")
//      .master("local[*]") -Dspark.master=local[*]
      .getOrCreate()
    val ssc = new StreamingContext(spark.sparkContext, Seconds(20))
    ssc.checkpoint("checkpoint")

    val topicMap = Map("Log-Events" -> 1)
    //TODO: move into the configuration!!!!!
    val lines = KafkaUtils.createStream(ssc, "localhost:2181", "test", topicMap).filter(line => !line._2.isEmpty)

    lines.map(con => con._2).foreachRDD( rdd => {
      val spark = SparkSession.builder.config(rdd.sparkContext.getConf).getOrCreate()
      rdd.saveAsTextFile(workingDirectory+"/data/ingest")
      val json = spark.read.json(rdd)
      json.show()
    })

    ssc.start()
    ssc.awaitTermination()

  }

}
