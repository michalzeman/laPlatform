package com.la.platform.batch.transform

import com.la.platform.batch.cli.{CliParams, DataJobMain}
import com.la.platform.batch.data.LabeledPoints.transformData
import org.apache.spark.sql.SparkSession


/**
 * Created by zemi on 09/11/2016.
 */
object TransformDataJob extends DataJobMain[CliParams] {

  override def appName: String = "TransformDataJob"

  override def run(spark: SparkSession, opt: CliParams): Unit = {

    val workingDir = opt.dataDir

    val labeledPoints = transformData(spark, workingDir)

    labeledPoints.write.format("libsvm")
      .save(s"$workingDir/../mllib/svm_data/lg_svm-${java.util.UUID.randomUUID.toString}")
  }

  override def getCliContext(args: Array[String]): CliParams = CliParams(args)

}
