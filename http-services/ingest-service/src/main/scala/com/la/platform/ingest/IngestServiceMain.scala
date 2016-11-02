package com.la.platform.ingest

import com.la.platform.ingest.actors.supervisors.IngestKafkaProducerSupervisorActor
import com.la.platform.ingest.rest.IngestRestService
import com.la.platform.rest.common.{AbstractMain, RestEndpointRoute}


/**
  * Created by zemi on 25/10/2016.
  */
object IngestServiceMain extends AbstractMain {

  val ingestKafkaProducerSupervisor = system.actorOf(IngestKafkaProducerSupervisorActor.props, IngestKafkaProducerSupervisorActor.ACTOR_NAME)

  /**
    * Init rest endpoints
    *
    * @return
    */
  override def initRestEndpoints: List[RestEndpointRoute] = List(new IngestRestService)

}
