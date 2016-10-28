package com.la.platform.batch.cli

import org.apache.commons.cli.{CommandLine, DefaultParser, HelpFormatter, Options, ParseException, Option => CliOption}

/**
  * Created by zemi on 28/10/2016.
  */
class CliContext(args:Array[String]) {

  protected val options = buildOptions

  protected val dataDirOpt = new CliOption("d", "dataDir", true, "Directory where are all data stored!")
  dataDirOpt.setRequired(true)
  options.addOption(dataDirOpt)

  protected val parser = new DefaultParser
  protected val formatter = new HelpFormatter

  private val cmd = parseCli()

  protected def buildOptions: Options = {
    val options = new Options
    val dataDirOpt = new CliOption("d", "dataDir", true, "Directory where are all data stored!")
    dataDirOpt.setRequired(true)
    options.addOption(dataDirOpt)
    options
  }

  private def parseCli(): CommandLine = {
    try {
      parser.parse(buildOptions, args);
    } catch {
      case e: ParseException => {
        formatter.printHelp("utility-name", options);
        //      System.exit(1);
        throw e;
      }
    }
  }

  def dataDir: String = cmd.getOptionValue("dataDir")

}

object CliContext {
  def apply(args: Array[String]): CliContext = new CliContext(args)
}
