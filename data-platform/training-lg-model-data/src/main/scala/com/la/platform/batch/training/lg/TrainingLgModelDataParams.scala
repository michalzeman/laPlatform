package com.la.platform.batch.training.lg

import com.la.platform.batch.cli.CliParams
import org.apache.commons.cli.Options

/**
  * Created by zemi on 31/10/2016.
  */
class TrainingLgModelDataParams(args: Array[String]) extends CliParams(args) {

  override protected def buildOptions: Options = {
    val zkUrl = buildOption("zkC", "zkUrl", true, "Url to zookeeper service")
    super.buildOptions.addOption(zkUrl)
  }

  def getZkUrl: String = cmd.getOptionValue("zkUrl")

}


object TrainingLgModelDataParams {
  def apply(args: Array[String]): TrainingLgModelDataParams = new TrainingLgModelDataParams(args)
}