package com.la.platform.ingest.bus

import akka.actor.Extension

/**
  * Created by zemi on 21/07/2017.
  */
class IngestEventBusExtensionImpl extends Extension {
  val eventBus = new IngestEventBus
}
