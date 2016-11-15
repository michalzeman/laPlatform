package com.la.platform.batch.transform

import com.la.platform.batch.cli.{CliParams, DataJobMain}
import org.apache.spark.sql.SparkSession
import com.la.platform.batch.services.TransformDataService
import org.apache.spark.mllib.util.MLUtils


/**
  * Created by zemi on 09/11/2016.
  */
object TransformDataJob extends DataJobMain[CliParams] {

  override def appName: String = "TransformDataJob"

  override def run(spark: SparkSession, opt: CliParams): Unit = {

    val workingDir = opt.dataDir
    TransformDataService.getInstance.transformData(workingDir)
      .foreach(rdd =>
        MLUtils.saveAsLibSVMFile(rdd.rdd, s"$workingDir/../mllib/svm_data/lg_svm-${java.util.UUID.randomUUID.toString}"))
  }

  override def getCliContext(args: Array[String]): CliParams = CliParams(args)

}
