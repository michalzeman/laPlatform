name := "data-platform"

version := "1.0"

lazy val commonSettings = Seq(
  organization := "com.la.platform.batch",
  version := "1.0",
  scalaVersion := "2.11.8",
  scalacOptions := Seq("-unchecked", "-feature", "-deprecation", "-encoding", "utf8"),
  libraryDependencies ++= Seq(
    "org.apache.spark" %% "spark-core" % "2.0.0" % "provided",
    "org.apache.spark" %% "spark-sql" % "2.0.0" % "provided",
    "org.apache.spark" %% "spark-streaming" % "2.0.0" % "provided",
    "org.apache.spark" %% "spark-mllib" % "2.0.0" % "provided",
    "org.apache.spark" %% "spark-streaming" % "2.0.0" % "provided",
    "commons-cli" % "commons-cli" % "1.2" % "provided",
    "com.github.scopt" %% "scopt" % "3.5.0"
  ),
  assemblyMergeStrategy in assembly := {
    case PathList("com","la","platform", xs @ _*) => MergeStrategy.last
    case PathList("org","slf4j", xs @ _*) => MergeStrategy.last
    case PathList("org","aopalliance", xs @ _*) => MergeStrategy.last
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
  })

lazy val ingestDataSettings = Seq(
  libraryDependencies ++= Seq(
    "org.apache.spark" %% "spark-streaming-kafka-0-8" % "2.0.0",
    "org.apache.kafka" % "kafka_2.11" % "0.9.0.1",
    "org.apache.kafka" % "kafka-clients" % "0.9.0.1"
  )
)

lazy val commonDataApi = (project in file("common-data-api"))
  .settings(commonSettings: _*)

lazy val ingestData = (project in file("ingest-data"))
  .settings(commonSettings: _*)
  .settings(ingestDataSettings: _*)
  .dependsOn(commonDataApi)

lazy val dataPlatform =
  project.in(file("."))
    .aggregate(ingestData, commonDataApi)

resolvers += Resolver.mavenLocal