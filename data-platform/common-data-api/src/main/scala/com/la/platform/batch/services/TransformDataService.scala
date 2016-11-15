package com.la.platform.batch.services

import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Dataset

/**
  * Created by zemi on 15/11/2016.
  */
trait TransformDataService extends Serializable {

  def transformData(workingDir: String): RDD[Dataset[LabeledPoint]]

}

object TransformDataService {
  def getInstance: TransformDataService = new TransformDataServiceImpl()
}
