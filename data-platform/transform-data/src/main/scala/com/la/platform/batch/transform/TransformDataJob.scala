package com.la.platform.batch.transform

import com.la.platform.batch.cli.{CliParams, DataJobMain}
import org.apache.spark.sql.{Row, SparkSession}
import com.la.platform.batch.common.constants._
import com.sun.javafx.geom.transform.BaseTransform.Degree
import org.apache.spark.mllib.linalg.{Vector, Vectors}
import org.apache.spark.SparkContext
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.util.MLUtils


/**
  * Created by zemi on 09/11/2016.
  */
object TransformDataJob extends DataJobMain[CliParams] {

  override def appName: String = "TransformDataJob"

  override def run(spark: SparkSession, opt: CliParams): Unit = {

    import spark.implicits._

    val workingDir = opt.dataDir
    val svmModel = spark.sparkContext.wholeTextFiles(workingDir + INGEST_DATA_PREFIX_PATH_WILDCARD, 2)
      .map(item => item._2)
      .filter(file => !file.isEmpty)
      .map(file => {
        val sparkContext = SparkContext.getOrCreate
        val jsonRdd = sparkContext.parallelize(Seq(file))
        val sparkSession = SparkSession.builder.config(sparkContext.getConf).getOrCreate
        sparkSession.read.json(jsonRdd)
          .select("*").where("originator='LR_demo'")
          .select("message")
      })
      .map(sqlDf => sqlDf.map(row => {
        val strVector = row.getAs[String]("message")
        val stringArray = strVector.split(",")
        val label = stringArray(stringArray.length - 1).toDouble
        val features = Vectors.dense(mapFeatures(stringArray(0).toDouble, stringArray(1).toDouble, 6).toArray)
        new LabeledPoint(label, features)
      }))
    svmModel.foreach(rdd => {
      MLUtils.saveAsLibSVMFile(rdd.rdd, s"${workingDir}/../mllib/lg_svm-${java.util.UUID.randomUUID.toString}")
    })
  }

  def mapFeatures(x1: Double, x2: Double, degree: Int): List[Double] = {
    val features = for {i <- 1 to degree
      j <- 0 to i
    } yield Math.pow(x1, i - 1) * Math.pow(x2, j)
    1::features.toList
  }

  override def getCliContext(args: Array[String]): CliParams = CliParams(args)

}
