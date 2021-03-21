package com.la.platform.batch.ml

import org.apache.spark.ml.classification.LogisticRegressionModel
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.sql.Row

/**
 * Created by zemi on 15/11/2016.
 */
object LogisticRegressionUtils {

  /**
   * Make a prediction with given LR model
   *
   * @param row     - contains features for which is going to make a prediction
   * @param lrModel - Logistic regression model
   * @return - prediction result as a string
   */
  def predict(row: Row, lrModel: LogisticRegressionModel): String = {
    val data = row.getAs[String]("data")
    val features = Vectors.dense(data.split(" ").map(item => item.split(":")(1)).map(_.toDouble))
    lrModel.predict(features).toString
  }

}
