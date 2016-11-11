name := "data-platform"

version := "1.0"

val kafkaV = "0.10.0.1"

val sparkV = "2.0.0"

lazy val commonSettings = Seq(
  version := "1.0",
  scalaVersion := "2.11.8",
  scalacOptions := Seq("-unchecked", "-feature", "-deprecation", "-encoding", "utf8"),
  //TODO: resolve this problem with adding separate task for assembly where those dependencies would switch to provided
  //  libraryDependencies ++= Seq(
  //    "org.apache.spark" %% "spark-core" % "2.0.0" % "provided",
  //    "org.apache.spark" %% "spark-sql" % "2.0.0" % "provided",
  //    "org.apache.spark" %% "spark-streaming" % "2.0.0" % "provided",
  //    "org.apache.spark" %% "spark-mllib" % "2.0.0" % "provided",
  //    "org.apache.spark" %% "spark-streaming" % "2.0.0" % "provided",
  //    "commons-cli" % "commons-cli" % "1.2" % "provided",
  //    "com.github.scopt" %% "scopt" % "3.5.0"
  //  ),
  libraryDependencies ++= Seq(
    "org.apache.spark" %% "spark-core" % sparkV,
    "org.apache.spark" %% "spark-sql" % sparkV,
    "org.apache.spark" %% "spark-streaming" % sparkV,
    "org.apache.spark" %% "spark-mllib" % sparkV,
    "org.apache.spark" %% "spark-streaming" % sparkV,
    "commons-cli" % "commons-cli" % "1.2",
    "com.github.scopt" %% "scopt" % "3.5.0"
  ))

lazy val assemblySettings = Seq(
  assemblyMergeStrategy in assembly := {
    case PathList("com", "la", "platform", xs@_*) => MergeStrategy.last
    case PathList("org", "slf4j", xs@_*) => MergeStrategy.last
    case PathList("org", "aopalliance", xs@_*) => MergeStrategy.last
    case PathList("javax", "inject", xs@_*) => MergeStrategy.last
    case PathList("javax", "servlet", xs@_*) => MergeStrategy.last
    case PathList("javax", "activation", xs@_*) => MergeStrategy.last
    case PathList("org", "apache", xs@_*) => MergeStrategy.last
    case PathList("com", "google", xs@_*) => MergeStrategy.last
    case PathList("com", "esotericsoftware", xs@_*) => MergeStrategy.last
    case PathList("com", "codahale", xs@_*) => MergeStrategy.last
    case PathList("com", "yammer", xs@_*) => MergeStrategy.last
    case "about.html" => MergeStrategy.rename
    case "META-INF/ECLIPSEF.RSA" => MergeStrategy.last
    case "META-INF/mailcap" => MergeStrategy.last
    case "META-INF/mimetypes.default" => MergeStrategy.last
    case "plugin.properties" => MergeStrategy.last
    case "log4j.properties" => MergeStrategy.last
    case "stylesheet.css" => MergeStrategy.last
    case x =>
      val oldStrategy = (assemblyMergeStrategy in assembly).value
      oldStrategy(x)
  }
)

lazy val kafkaDepSettings = Seq(
  libraryDependencies ++= Seq(
    "org.apache.spark" % "spark-streaming-kafka-0-10_2.11" % sparkV,
    "org.apache.kafka" % "kafka_2.11" % kafkaV,
    "org.apache.kafka" % "kafka-clients" % kafkaV
  )
)

lazy val commonDataApiSettings = Seq(
  organization := "com.la.platform.batch"
)

lazy val commonDataApi = (project in file("common-data-api"))
  .settings(commonSettings: _*)
  .settings(commonDataApiSettings: _*)
  .settings(assemblySettings: _*)
  .settings(kafkaDepSettings: _*)

lazy val ingestDataSettings = Seq(
  organization := "com.la.platform.batch.ingest"
)

lazy val ingestData = (project in file("ingest-data"))
  .settings(commonSettings: _*)
  .settings(ingestDataSettings: _*)
  .settings(kafkaDepSettings: _*)
  .settings(assemblySettings: _*)
  .dependsOn(classpathDependency(commonDataApi))

lazy val trainingLgModelData = (project in file("training-lg-model-data"))
  .settings(commonSettings: _*)
  .dependsOn(classpathDependency(commonDataApi))

lazy val predictDataSettings = Seq(
  organization := "com.la.platform.batch.predict"
)

lazy val predictData = (project in file("predict-data"))
  .settings(commonSettings: _*)
  .settings(predictDataSettings: _*)
  .settings(kafkaDepSettings: _*)
  .settings(assemblySettings: _*)
  .dependsOn(classpathDependency(commonDataApi))

lazy val transformData = (project in file("transform-data"))
  .settings(commonSettings: _*)
  .settings(assemblySettings: _*)
  .settings(Seq(organization := "com.la.platform.batch.transform"): _*)
  .dependsOn(classpathDependency(commonDataApi))

lazy val dataPlatform =
  project.in(file("."))
    .aggregate(ingestData, predictData, transformData, trainingLgModelData)


resolvers += Resolver.mavenLocal