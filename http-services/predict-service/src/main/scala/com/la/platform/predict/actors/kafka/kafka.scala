package com.la.platform.predict.actors

/**
  * Created by zemi on 03/11/2016.
  */
package object kafka {

  case class PredictKafkaConsumerMsg(result: String)

  case class PredictionJsonMsg(data: String, sender: String)

  case class PredictKafkaProducerMsg(data: String)

  case class PredictKafkaProducerUnsupportedOpr(msg: String)
}
