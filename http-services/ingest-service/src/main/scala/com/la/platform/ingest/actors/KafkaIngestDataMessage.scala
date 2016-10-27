package com.la.platform.ingest.actors

/**
  * Created by zemi on 27/10/2016.
  */
case class KafkaIngestDataMessage(message:String, originator:Option[String], time: String)
