package com.la.platform.batch.ingest

import com.la.platform.batch.cli.CliContext

/**
  * Created by zemi on 28/10/2016.
  */
class IngestDataCliContext(args: Array[String]) extends CliContext(args) {

}

object IngestDataCliContext {
  def apply(args: Array[String]): IngestDataCliContext = new IngestDataCliContext(args)
}