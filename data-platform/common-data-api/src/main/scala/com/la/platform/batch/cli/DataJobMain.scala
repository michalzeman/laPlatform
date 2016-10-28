package com.la.platform.batch.cli

import org.apache.spark.sql.SparkSession

/**
  * Created by zemi on 28/10/2016.
  */
trait DataJobMain[C <: CliContext] {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession
      .builder
      .appName(appName)
      //      .master("local[*]") -Dspark.master=local[*]
      .getOrCreate()

    run(spark, getCliContext(args))
  }

  def appName: String

  def run(spark: SparkSession, opt: CliContext): Unit

  def getCliContext(args:Array[String]): C
}
