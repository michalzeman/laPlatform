package com.la.platform.ingest.model

import spray.json.DefaultJsonProtocol._
/**
  * Created by zemi on 25/10/2016.
  */
case class IngestData(data: String, originator:Option[String])

object IngestData {
  implicit val format = jsonFormat2(IngestData.apply)
}
