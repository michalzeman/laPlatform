//enablePlugins(JavaAppPackaging)

name := "http-services"

val akkaV = "2.4.8"
val kafkaV = "0.10.0.1"
val scalaTestV = "2.2.6"
val sparkV = "2.0.0"

lazy val commonSettings = Seq(
  organization := "com.la.platform.http",
  version := "1.0",
  scalaVersion := "2.11.8",
  scalacOptions := Seq("-unchecked", "-feature", "-deprecation", "-encoding", "utf8"),
  libraryDependencies ++= Seq(
    "com.typesafe" % "config" % "1.3.0",
    "com.typesafe.akka" %% "akka-http-core" % akkaV,
    "com.typesafe.akka" %% "akka-stream" % akkaV,
    "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaV,
    "com.typesafe.akka" % "akka-http-testkit-experimental_2.11" % "2.4.2-RC3",
    "com.google.inject" % "guice" % "4.1.0",
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "ch.qos.logback" % "logback-classic" % "1.1.3",
    "com.typesafe.akka" %% "akka-slf4j" % akkaV,
    "com.typesafe.akka" %% "akka-testkit" % akkaV % "test",
    "org.mockito" % "mockito-all" % "1.10.19" % "test",
    "org.scalamock" %% "scalamock-scalatest-support" % "3.2.2" % "test",
    "org.scalatest" %%"scalatest" % "3.0.0" % "test",
    //DB dependencies
    "org.postgresql" % "postgresql" % "9.4-1203-jdbc42",
    "com.zaxxer" % "HikariCP" % "2.4.1",
    "org.apache.kafka" %% "kafka" % kafkaV,
    "org.apache.kafka" % "kafka-clients" % kafkaV,
    "net.liftweb" % "lift-json_2.11" % "2.6.3"
  ),
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
    case x =>
      val oldStrategy = (assemblyMergeStrategy in assembly).value
      oldStrategy(x)
  }
)

lazy val apacheSparkSettings = Seq(
  libraryDependencies ++= Seq(
    "org.apache.spark" %% "spark-core" % sparkV,
    "org.apache.spark" %% "spark-sql" % sparkV,
    "org.apache.spark" %% "spark-streaming" % sparkV,
    "org.apache.spark" %% "spark-mllib" % sparkV,
    "org.apache.spark" %% "spark-streaming" % sparkV,
    "org.apache.hadoop" % "hadoop-client" % "2.7.3")
)

lazy val commonApi = (project in file("common-api"))
  .settings(commonSettings: _*)

lazy val ingestServiceSettings = Seq(
  name := "ingest-service",
  mainClass in assembly := Some("com.la.platform.ingest.IngestMain")
)

lazy val predictServiceSettings = Seq(
  libraryDependencies ++= Seq("com.typesafe.akka" %% "akka-stream-kafka" % "0.13")
)

lazy val predictService = (project in file("predict-service"))
  .settings(commonSettings: _*)
  .settings(apacheSparkSettings: _*)
  .settings(predictServiceSettings: _*)
  .dependsOn(classpathDependency(commonApi))

lazy val ingestService = (project in file("ingest-service"))
  .settings(commonSettings: _*)
  .settings(ingestServiceSettings: _*)
  .dependsOn(classpathDependency(commonApi))

lazy val `speed-service` = (project in file("speed-service"))
  .settings(commonSettings: _*)
  .settings(predictServiceSettings: _*)
  .dependsOn(classpathDependency(commonApi))

lazy val httpServices =
  project.in(file("."))
    .aggregate(predictService, ingestService, `speed-service`)



