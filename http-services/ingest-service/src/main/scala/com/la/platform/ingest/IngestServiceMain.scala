package com.la.platform.ingest

import com.la.platform.ingest.actors.supervisors.IngestKafkaProducerSupervisorActor
import com.la.platform.ingest.common.util.Settings
import com.la.platform.ingest.rest.IngestRestService
import com.la.platform.ingest.rest.common.{AbstractMain, RestEndpointRoute}
import com.typesafe.config.Config


/**
  * Created by zemi on 25/10/2016.
  */
object IngestServiceMain extends AbstractMain {

  lazy val settings = Settings(system.settings.config)

  val ingestKafkaProducerSupervisor = system.actorOf(IngestKafkaProducerSupervisorActor.props, IngestKafkaProducerSupervisorActor.ACTOR_NAME)

  /**
    * Init rest endpoints
    *
    * @return
    */
  override def initRestEndpoints: List[RestEndpointRoute] = List(new IngestRestService)

  override def getHttpInterface(config: Config): String = settings.Http.interface

  override def getHttpPort(config: Config): Int = settings.Http.port
}
