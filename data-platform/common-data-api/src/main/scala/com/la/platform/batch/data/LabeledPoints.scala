package com.la.platform.batch.data

import com.la.platform.batch.common.constants._
import org.apache.spark.ml.feature.LabeledPoint
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.sql.{Dataset, SparkSession}

/**
 * Created by zemi on 15/11/2016.
 */
object LabeledPoints {

  def transformData(spark: SparkSession, workingDir: String): Dataset[LabeledPoint] = {
    import spark.implicits._

    val filesDs = spark.read.json(
      spark.sparkContext.wholeTextFiles(workingDir + INGEST_DATA_PREFIX_PATH_WILDCARD, 2)
        .map(_._2)
        .filter(_.nonEmpty).toDS()
    )

    val messageDs = filesDs.select("*").where("originator='LR_demo'")
      .select("message")

    messageDs.map(row => {
      val strVector = row.getAs[String]("message")
      val stringArray = strVector.split(",")
      val label = stringArray(stringArray.length - 1).toDouble
      val features = Vectors.dense(mapFeatures(stringArray(0).toDouble, stringArray(1).toDouble, 6).toArray)
      new LabeledPoint(label, features)
    })
  }

  def mapFeatures(x1: Double, x2: Double, degree: Int): List[Double] = {
    val features = for {i <- 1 to degree
                        j <- 0 to i
                        } yield Math.pow(x1, i - 1) * Math.pow(x2, j)
    1 :: features.toList
  }
}
