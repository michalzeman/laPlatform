package com.la.platform.predict.rest

import spray.json.DefaultJsonProtocol._

/**
  * Created by zemi on 02/11/2016.
  */
case class PredictResponse(result: String)

object PredictResponse {
  implicit val format = jsonFormat1(PredictResponse.apply)
}
