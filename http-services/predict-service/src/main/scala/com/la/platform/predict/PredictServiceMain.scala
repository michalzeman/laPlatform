package com.la.platform.predict

import com.la.platform.predict.rest.PredictRestService
import com.la.platform.rest.common.{AbstractMain, RestEndpointRoute}

/**
  * Created by zemi on 02/11/2016.
  */
object PredictServiceMain extends AbstractMain {

  /**
    * Init rest endpoints
    *
    * @return
    */
  override def initRestEndpoints: List[RestEndpointRoute] = List(new PredictRestService)

}
