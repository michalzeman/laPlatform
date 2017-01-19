package com.la.platform.predict.actors

/**
  * Created by zemi on 03/11/2016.
  */
package object kafka {

//  case class PredictResponseMsg(result: String)

  case class PredictReloadModelJsonMsg(data: String, sender: String)

//  case class PredictRequestMsg(data: String)

  case object PredictRequestMsgSent

  case class PredictKafkaProducerUnsupportedOpr(msg: String)
}
