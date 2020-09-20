package com.la.platform.batch.training.lg

import java.util
import java.util.UUID

import com.la.platform.batch.cli.DataJobMain
import com.la.platform.batch.common.constants._
import com.la.platform.batch.kafka.KafkaProducerWrapper
import com.la.platform.batch.ml.SvmIOUtils.loadDDRLibSVMFiles
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.feature.LabeledPoint
import org.apache.spark.sql.{Row, SparkSession}

/**
  * Created by zemi on 11/11/2016.
  */
object TrainingLgModelDataJob extends DataJobMain[TrainingLgModelDataParams] {

  override def appName: String = "TrainingLgModelDataJob"

  override def run(spark: SparkSession, opt: TrainingLgModelDataParams): Unit = {
    import spark.implicits._

    val kafkaPredictionResultProducer = spark.sparkContext.broadcast(KafkaProducerWrapper(getKafkaProducerProp(opt)))

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
    //notify all services using ML model that there is new version of model
    val reloadModelMsg =s"""{"data":"New Logistic regression model is available. Path: ${opt.dataDir}lr/model","sender":"${appName}"}""".stripMargin
    kafkaPredictionResultProducer.value.send("PredictReloadModel", UUID.randomUUID().toString, reloadModelMsg)

    predictionsEval.show()
    val eval = predictionsEval.select("label", "prediction")
      .map{case Row(label, prediction) => if (label == prediction) 1 else 0}.cache
    eval.show()
    println(eval.reduce(_+_))
  }

  def getKafkaProducerProp(opt: TrainingLgModelDataParams): java.util.Map[String, Object] = {
    val props = new util.HashMap[String, Object]()
    props.put("bootstrap.servers", opt.getZkUrl)
    props.put("client.id", appName)
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props
  }

  override def getCliContext(args: Array[String]): TrainingLgModelDataParams = TrainingLgModelDataParams(args)

}
