package com.la.platform.predict

import com.la.platform.common.rest.{AbstractMain, RestEndpointRoute}
import com.la.platform.predict.actors.supervisors.{PredictKafkaSupervisorActor, PredictSupervisorActor}
import com.la.platform.predict.rest.PredictRestService

/**
  * Created by zemi on 02/11/2016.
  */
object PredictServiceMain extends AbstractMain {

  val predictKafkaSupervisorActor = system.actorOf(PredictKafkaSupervisorActor.props, PredictKafkaSupervisorActor.ACTOR_NAME)

  val predictSupervisorActor = system.actorOf(PredictSupervisorActor.props, PredictSupervisorActor.actor_name)

  /**
    * Init rest endpoints
    *
    * @return
    */
  override def initRestEndpoints: List[RestEndpointRoute] = List(new PredictRestService)

}
