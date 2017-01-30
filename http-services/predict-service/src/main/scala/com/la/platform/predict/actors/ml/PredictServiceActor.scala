package com.la.platform.predict.actors.ml

import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern._
import akka.util.Timeout
import com.la.platform.predict.actors.PredictActorSelection
import com.la.platform.predict.actors.ml.PredictServiceActor.PredictionResult
import org.apache.spark.ml.classification.LogisticRegressionModel
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.sql.SparkSession

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Future, Promise}

/**
  * Created by zemi on 14/12/2016.
  */
class PredictServiceActor extends Actor with ActorLogging with PredictActorSelection {

  protected implicit val timeout: Timeout = 2000.milliseconds

  val modelPath: String = context.system.settings.config.getString("ml.logistic.regression.model.path")

  val spark: SparkSession = SparkSession
    .builder
    .appName("PredictService")
    .master("local[*]")
    .getOrCreate()

  var lrModel: Option[LogisticRegressionModel] = loadMlModel


  override def receive: Receive = {
    case msg: PredictServiceActor.PredictRequestMsg => predict(msg) pipeTo sender
    case PredictServiceActor.ReloadMlModel => {
      log.debug(s"${getClass.getCanonicalName} -> ReloadMlModel")
      lrModel = loadMlModel
    }
    case msg: Any => log.warning(s"${getClass.getCanonicalName} received unsupported message: ${msg.getClass}")
  }

  /**
    * Load Ml model
    *
    * @return Option[LogisticRegressionModel]
    */
  private def loadMlModel: Option[LogisticRegressionModel] = {
    Some(LogisticRegressionModel
      .load(modelPath))
  }

  /**
    * make prediction
    *
    * @param msg
    * @return
    */
  private def predict(msg: PredictServiceActor.PredictRequestMsg): Future[PredictServiceActor.PredictResponseMsg] = {
    val p = Promise[PredictServiceActor.PredictResponseMsg]
    Future {
      lrModel.foreach(model => {
        val features = Vectors.dense(msg.data.split(" ").map(item => item.split(":")(1)).map(_.toDouble))
        val forPredictionDF = spark.createDataFrame(Seq((0.0, features))).toDF("label", "features").select("features")
        val predResultList = model.transform(forPredictionDF).select("prediction").collect().toList
        val result = if (predResultList.isEmpty) "error" else predResultList.head.get(0).toString
        p.success(PredictServiceActor.PredictResponseMsg(result))
        selectPredictionResultKafkaProducerActor ! PredictionResult(result, msg.data)
      })
    }
    p.future
  }

}

object PredictServiceActor {

  case object ReloadMlModel

  case class PredictRequestMsg(data: String)

  case class PredictResponseMsg(result: String)

  case class PredictionResult(prediction: String, data: String)

  val actor_name = "PredictServiceActor"

  def props: Props = Props[PredictServiceActor]
}