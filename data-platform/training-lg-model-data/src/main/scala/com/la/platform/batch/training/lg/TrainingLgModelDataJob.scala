package com.la.platform.batch.training.lg

import com.la.platform.batch.cli.DataJobMain
import com.la.platform.batch.common.constants._
import com.la.platform.batch.kafka.KafkaProducerWrapper
import org.apache.spark.ml.classification.{LogisticRegression, OneVsRest}
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.sql.SparkSession

import java.util
import java.util.UUID

/**
 * Created by zemi on 11/11/2016.
 */
object TrainingLgModelDataJob extends DataJobMain[TrainingLgModelDataParams] {

  override def appName: String = "TrainingLgModelDataJob"

  override def run(spark: SparkSession, opt: TrainingLgModelDataParams): Unit = {

    val kafkaPredictionResultProducer = spark.sparkContext.broadcast(KafkaProducerWrapper(getKafkaProducerProp(opt)))

    val dataDir = opt.dataDir

    val classifier = new LogisticRegression()
      .setMaxIter(1000)
      .setElasticNetParam(0.8)
      .setTol(1E-6)
      .setFitIntercept(true)

    val ovr = new OneVsRest().setClassifier(classifier)

    val dataDf = spark.read.format("libsvm")
      .load(s"$dataDir/$LG_SVM_PATH_WILDCARD").cache()

    val splits = dataDf.randomSplit(Array(0.8, 0.2))

    val (trainingData, testData) = (splits(0), splits(1))


    val lrModel = ovr.fit(trainingData)
    // score the model on test data.
    val predictions = lrModel.transform(testData)

    // obtain evaluator.
    val evaluator = new MulticlassClassificationEvaluator()
      .setMetricName("accuracy")

    // compute the classification error on test data.
    val accuracy = evaluator.evaluate(predictions)
    println(s"Accuracy = $accuracy")
    println(s"Test Error = ${1 - accuracy}")

//    val reloadModelMsg = s"""{"data":"New Logistic regression model is available. Path: ${opt.dataDir}lr/model","sender":"${appName}"}""".stripMargin
//    kafkaPredictionResultProducer.value.send("PredictReloadModel", UUID.randomUUID().toString, reloadModelMsg)
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
