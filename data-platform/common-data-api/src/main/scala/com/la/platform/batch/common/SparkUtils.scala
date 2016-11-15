package com.la.platform.batch.common

import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession

/**
  * Created by zemi on 15/11/2016.
  */
trait SparkUtils {

  def getSparkSession: SparkSession = SparkSession.builder.config(SparkContext.getOrCreate().getConf).getOrCreate()

  def getSparkContext: SparkContext = SparkContext.getOrCreate()

}
