package com.la.platform.ingest.actors

import spray.json.DefaultJsonProtocol._

/**
  * Created by zemi on 27/10/2016.
  */
case class KafkaIngestDataMessage(message:String, originator:Option[String], time: String)

object KafkaIngestDataMessage {
  implicit val format = jsonFormat3(KafkaIngestDataMessage.apply)
}
