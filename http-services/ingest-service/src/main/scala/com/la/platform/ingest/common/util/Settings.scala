package com.la.platform.ingest.common.util

import akka.actor.Extension
import com.typesafe.config._

class Settings(config: Config) extends Extension {

  object Http {
    lazy val interface = config.getString("http.interface")
    lazy val port = config.getInt("http.port")
  }
}

object Settings {

  def apply(config: Config): Settings = new Settings(config)
}
