package com.la.platform.batch.cli

import org.apache.commons.cli.{CommandLine, BasicParser, HelpFormatter, Options, ParseException, Option => CliOption}

/**
  * Created by zemi on 28/10/2016.
  */
class CliParams(args:Array[String]) {

  protected val options = buildOptions

  protected val parser = new BasicParser
  protected val formatter = new HelpFormatter

  protected val cmd = parseCli()

  protected def buildOptions: Options = {
    val options = new Options
    val dataDirOpt = new CliOption("d", "dataDir", true, "Directory where are all data stored!")
    dataDirOpt.setRequired(true)
    options.addOption(dataDirOpt)
    options
  }

  private def parseCli(): CommandLine = {
    try {
      parser.parse(options, args)
    } catch {
      case e: ParseException => {
        formatter.printHelp("utility-name", options)
        throw e
      }
    }
  }

  def dataDir: String = cmd.getOptionValue("dataDir")

}

object CliParams {
  def apply(args: Array[String]): CliParams = new CliParams(args)
}
