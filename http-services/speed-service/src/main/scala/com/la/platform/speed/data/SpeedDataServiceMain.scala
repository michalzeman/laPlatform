package com.la.platform.speed.data

import com.la.platform.common.rest.{AbstractMain, RestEndpointRoute}
import com.la.platform.speed.data.rest.SpeedDataRestService

/**
  * Created by zemi on 02/12/2016.
  */
object SpeedDataServiceMain extends AbstractMain {
  /**
    * Init rest endpoints
    *
    * @return
    */
  override def initRestEndpoints: List[RestEndpointRoute] = List(new SpeedDataRestService)
}
