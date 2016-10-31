package com.la.platform.batch.ingest

import com.la.platform.batch.cli.CliParams
import org.apache.commons.cli.{Option => CliOption, Options}

/**
  * Created by zemi on 28/10/2016.
  */
class IngestDataCliParams(args: Array[String]) extends CliParams(args) {
  override protected def buildOptions: Options = {
    val zkUrl = new CliOption("z", "zkUrl", true, "Url to zookeeper service")
    super.buildOptions.addOption(zkUrl)
  }

  def getZkUrl: String = cmd.getOptionValue("zkUrl")
}

object IngestDataCliParams {
  def apply(args: Array[String]): IngestDataCliParams = new IngestDataCliParams(args)
}