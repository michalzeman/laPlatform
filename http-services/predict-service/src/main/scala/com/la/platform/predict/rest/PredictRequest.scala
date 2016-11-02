package com.la.platform.predict.rest

import spray.json.DefaultJsonProtocol._

/**
  * Created by zemi on 02/11/2016.
  */
case class PredictRequest(data: String)


object PredictRequest {
  implicit val format = jsonFormat1(PredictRequest.apply)
}