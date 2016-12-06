package com.la.platform.batch.training.lg

import com.la.platform.batch.cli.{CliParams, DataJobMain}
import com.la.platform.batch.ml.SvmIOUtils.loadDDRLibSVMFiles
import com.la.platform.batch.common.constants._

import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.feature.LabeledPoint
import org.apache.spark.sql.{Row, SparkSession}

/**
  * Created by zemi on 11/11/2016.
  */
object TrainingLgModelDataJob extends DataJobMain[CliParams] {

  override def appName: String = "TrainingLgModelDataJob"

  override def run(spark: SparkSession, opt: CliParams): Unit = {
    import spark.implicits._

    val dataDir = opt.dataDir

    val lr = new LogisticRegression()
      .setMaxIter(100)
      .setRegParam(0.3)
      .setElasticNetParam(0.2)

    val dataSet = loadDDRLibSVMFiles(s"$dataDir$LG_SVM_PATH_WILDCARD").map(item => LabeledPoint(item.label,item.features.asML)).toDF.cache

    val splits = dataSet.randomSplit(Array(0.8, 0.2))

    val (trainingData, testData) = (splits(0), splits(1))


    val lrModel = lr.fit(trainingData)
    lrModel.write.overwrite.save(s"${opt.dataDir}lr/model")
    val predictionsEval = lrModel.evaluate(testData).predictions.cache
    predictionsEval.show()
    val eval = predictionsEval.select("label", "prediction")
      .map{case Row(label, prediction) => if (label == prediction) 1 else 0}.cache
    eval.show()
    println(eval.reduce(_+_))
  }

  override def getCliContext(args: Array[String]): CliParams = CliParams(args)

}
