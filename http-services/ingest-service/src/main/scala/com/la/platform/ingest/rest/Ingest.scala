package com.la.platform.ingest.rest

import spray.json.DefaultJsonProtocol._
/**
  * Created by zemi on 25/10/2016.
  */
case class Ingest(data: String, originator:Option[String])

object Ingest {
  implicit val format = jsonFormat2(Ingest.apply)
}
