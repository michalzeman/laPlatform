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
    val dataDirOpt = buildOption("d", "dataDir", true, "Directory where are all data stored!")
    dataDirOpt.setRequired(true)
    options.addOption(dataDirOpt)
    options
  }

  /**
    * Build/create single option and return it
    * @param opt - short option name
    * @param longOpt - long option name
    * @param hasArg - required
    * @param description - option description
    * @return - created option
    */
  protected def buildOption(opt: String, longOpt: String, hasArg: Boolean, description: String): CliOption = new CliOption(opt, longOpt, hasArg, description)

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
