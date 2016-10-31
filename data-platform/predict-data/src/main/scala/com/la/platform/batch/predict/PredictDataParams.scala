package com.la.platform.batch.predict

import com.la.platform.batch.cli.CliParams
import org.apache.commons.cli.Options

/**
  * Created by zemi on 31/10/2016.
  */
class PredictDataParams(args: Array[String]) extends CliParams(args) {

  override protected def buildOptions: Options = {
    val zkUrl = buildOption("zkC", "zkUrl", true, "Url to zookeeper service")
    val zkProducerUrl = buildOption("zkP", "zkProducerUrl", true, "Url to zookeeper service producer")
    super.buildOptions.addOption(zkUrl).addOption(zkProducerUrl)
  }

  def getZkUrl: String = cmd.getOptionValue("zkUrl")

  def getZkProducer:String = cmd.getOptionValue("zkProducerUrl")

}


object PredictDataParams {
  def apply(args: Array[String]): PredictDataParams = new PredictDataParams(args)
}