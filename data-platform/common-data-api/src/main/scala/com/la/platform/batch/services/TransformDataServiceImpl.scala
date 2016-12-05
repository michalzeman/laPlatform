package com.la.platform.batch.services
import com.la.platform.batch.common.SparkUtils
import com.la.platform.batch.common.constants._
import org.apache.spark.SparkContext
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Dataset, SparkSession}

/**
  * Created by zemi on 15/11/2016.
  */
class TransformDataServiceImpl extends TransformDataService with SparkUtils{

  override def transformData(workingDir: String): RDD[Dataset[LabeledPoint]] = {
    val spark = getSparkSession
    import spark.implicits._
    spark.sparkContext.wholeTextFiles(workingDir + INGEST_DATA_PREFIX_PATH_WILDCARD, 2)
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
  }

  def mapFeatures(x1: Double, x2: Double, degree: Int): List[Double] = {
    val features = for {i <- 1 to degree
                        j <- 0 to i
    } yield Math.pow(x1, i - 1) * Math.pow(x2, j)
    1::features.toList
  }
}
