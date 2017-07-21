package com.la.platform.ingest

import com.la.platform.common.rest.{AbstractMain, RestEndpointRoute}
import com.la.platform.ingest.actors.supervisors.IngestKafkaProducerSupervisorActor
import com.la.platform.ingest.rest.IngestRestService


/**
  * Created by zemi on 25/10/2016.
  */
object IngestServiceMain extends AbstractMain {

  val ingestKafkaProducerSupervisor = system.actorOf(IngestKafkaProducerSupervisorActor.props, IngestKafkaProducerSupervisorActor.ACTOR_NAME)

  val restServices = List(new IngestRestService)

  /**
    * Init rest endpoints
    *
    * @return
    */
  override def initRestEndpoints: List[RestEndpointRoute] = restServices

}
