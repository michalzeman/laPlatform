package com.la.platform.predict.actors.ml

import akka.actor.{ActorRef, ActorSystem}
import org.apache.spark.ml.classification.LogisticRegressionModel
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.sql.SparkSession

import scala.util.{Failure, Success, Try}

/**
 * Created by zemi on 08/10/2017.
 */
trait LogisticRegressionProvider {

  def predict(data: String): Option[String]

  def reloadMlModel(): Unit

}

object LogisticRegressionProvider {
  def apply(supervisor: ActorRef, system: ActorSystem): LogisticRegressionProvider =
    new LogisticRegressionProviderImpl(supervisor, system)
}

private class LogisticRegressionProviderImpl(supervisor: ActorRef, system: ActorSystem) extends LogisticRegressionProvider {

  val modelPath: String = system.settings.config.getString("ml.logistic.regression.model.path")

  val sparkOpt: Option[SparkSession] = getSparkSession match {
    case Success(session) => Some(session)
    case Failure(error) => {
      supervisor ! error
      None
    }
  }

  private def getSparkSession: Try[SparkSession] = {
    Try(SparkSession
      .builder
      .appName("PredictService")
      .master("local[*]")
      .getOrCreate())
  }

  private def loadMlModel(): Option[LogisticRegressionModel] = Some(LogisticRegressionModel.load(modelPath))

  var lrModelOpt: Option[LogisticRegressionModel] = loadMlModel()

  override def predict(data: String): Option[String] =
    lrModelOpt.flatMap(model =>
      sparkOpt.map(spark => {
        val features = Vectors.dense(
          data.split("(\\s)+")
            .map(item => item.split(":")(1))
            .map(_.toDouble)
        )
        model.predict(features).toString
      })
    )


  /**
   * Load Ml model
   *
   * @return Option[LogisticRegressionModel]
   */
  override def reloadMlModel(): Unit = {
    lrModelOpt = loadMlModel()
  }

}
