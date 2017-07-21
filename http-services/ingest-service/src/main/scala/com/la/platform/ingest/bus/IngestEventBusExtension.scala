package com.la.platform.ingest.bus

import akka.actor.{ActorSystem, ExtendedActorSystem, Extension, ExtensionId, ExtensionIdProvider}

/**
  * Created by zemi on 21/07/2017.
  */
object IngestEventBusExtension extends ExtensionId[IngestEventBusExtensionImpl] with ExtensionIdProvider {

  override def createExtension(system: ExtendedActorSystem): IngestEventBusExtensionImpl = new IngestEventBusExtensionImpl

  override def lookup(): ExtensionId[_ <: Extension] = IngestEventBusExtension
}
