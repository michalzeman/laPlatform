package com.la.platform.batch.training.lg

import com.la.platform.batch.cli.{CliParams, DataJobMain}
import org.apache.spark.sql.SparkSession

/**
  * Created by zemi on 11/11/2016.
  */
object TrainingLgModelDataJob extends DataJobMain[CliParams] {

  override def appName: String = "TrainingLgModelDataJob"

  override def run(spark: SparkSession, opt: CliParams): Unit = {

  }

  override def getCliContext(args: Array[String]): CliParams = CliParams(args)

}
