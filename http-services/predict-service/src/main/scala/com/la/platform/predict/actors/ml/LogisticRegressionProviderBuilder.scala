package com.la.platform.predict.actors.ml

import akka.actor.{ActorRef, ActorSystem}

/**
  * Created by zemi on 08/10/2017.
  */
trait LogisticRegressionProviderBuilder {
  def build(supervisor: ActorRef, system: ActorSystem): LogisticRegressionProvider
}

object LogisticRegressionProviderBuilder {
 def get: LogisticRegressionProviderBuilder = new LogisticRegressionProviderBuilderImpl()
}

private[ml] class LogisticRegressionProviderBuilderImpl extends LogisticRegressionProviderBuilder {
  override def build(supervisor: ActorRef, system: ActorSystem): LogisticRegressionProvider =
    LogisticRegressionProvider(supervisor, system)
}
