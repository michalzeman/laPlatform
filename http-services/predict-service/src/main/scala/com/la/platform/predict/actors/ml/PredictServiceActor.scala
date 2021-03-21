package com.la.platform.predict.actors.ml

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.util.Timeout
import com.la.platform.predict.actors.ml.PredictServiceActor.{MlModelReloaded, PredictRequest, PredictionResult, ReloadMlModel}
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContextExecutor, Future}

/**
  * Created by zemi on 14/12/2016.
  */
class PredictServiceActor(logisticRegressionBuilder: LogisticRegressionProviderBuilder) extends Actor with ActorLogging {

  protected implicit val timeout: Timeout = 2000.milliseconds

  implicit val dispatcher: ExecutionContextExecutor  = context.dispatcher

  context.system.eventStream.subscribe(self, classOf[PredictRequest])
  context.system.eventStream.subscribe(self, classOf[ReloadMlModel])

  val logisticRegressionProvider: LogisticRegressionProvider = logisticRegressionBuilder.build(self, context.system)


  override def receive: Receive = {
    case cmd: PredictRequest => predict(cmd)
    case cmd: ReloadMlModel => {
      log.debug(s"${getClass.getCanonicalName} -> ReloadMlModel")
      logisticRegressionProvider.reloadMlModel()
      sender ! MlModelReloaded
    }
    case msg: Any => {
      log.warning(s"${getClass.getCanonicalName} received unsupported message: ${msg.getClass}")
    }
  }

  /**
    * make prediction
    *
    * @param cmd
    * @return
    */
  private def predict(cmd: PredictRequest): Unit = {
    Future {
      val result = logisticRegressionProvider.predict(cmd.data).getOrElse("error")
      cmd.requester ! PredictServiceActor.PredictResponse(result)
      context.system.eventStream.publish(PredictionResult(result, cmd.data))
    }
  }

  override def preStart(): Unit = {
  }

  override def postStop(): Unit = {
    context.system.eventStream.unsubscribe(self, classOf[PredictRequest])
    context.system.eventStream.unsubscribe(self, classOf[ReloadMlModel])
  }
}

object PredictServiceActor {

  case class ReloadMlModel()

  object MlModelReloaded

  case class PredictRequest(data: String, requester: ActorRef)

  case class PredictResponse(result: String)

  case class PredictionResult(prediction: String, data: String)

  object PredictionResult {
    implicit val format: RootJsonFormat[PredictionResult] = jsonFormat2(PredictionResult.apply)
  }

  val actor_name = "PredictServiceActor"

  def props(logisticRegressionBuilder: LogisticRegressionProviderBuilder): Props =
    Props(new PredictServiceActor(logisticRegressionBuilder))
}