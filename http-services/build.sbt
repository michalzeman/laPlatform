//enablePlugins(JavaAppPackaging)

name := "http-services"

val akkaV = "2.5.1"
val akkaHttpV = "10.0.6"
val kafkaV = "2.6.0"
val scalaTestV = "2.2.6"
val sparkV = "2.4.7"
val akkaStreamKafkaV = "0.13"

lazy val commonSettings = Seq(
  organization := "com.la.platform.http",
  version := "1.0",
  scalaVersion := "2.12.11",
  scalacOptions := Seq("-unchecked", "-feature", "-deprecation", "-encoding", "utf8"),
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-stream-kafka" % akkaStreamKafkaV,
    "com.typesafe" % "config" % "1.3.0",
    "com.typesafe.akka" %% "akka-http-core" % akkaHttpV,
    "com.typesafe.akka" %% "akka-stream" % akkaV,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpV,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpV,
    "com.google.inject" % "guice" % "4.1.0",
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "ch.qos.logback" % "logback-classic" % "1.1.3",
    "com.typesafe.akka" %% "akka-slf4j" % akkaV,
    "com.typesafe.akka" %% "akka-testkit" % akkaV % "test",
    "org.mockito" % "mockito-all" % "1.10.19" % "test",
    "org.scalamock" %% "scalamock-scalatest-support" % "3.6.0" % "test",
    "org.scalatest" %%"scalatest" % "3.0.4" % "test",
    //DB dependencies
    "org.postgresql" % "postgresql" % "9.4-1203-jdbc42",
    "com.zaxxer" % "HikariCP" % "2.4.1",
    "org.apache.kafka" %% "kafka" % kafkaV,
    "org.apache.kafka" % "kafka-clients" % kafkaV,
    // https://mvnrepository.com/artifact/org.reactivestreams/reactive-streams
    "org.reactivestreams" % "reactive-streams" % "1.0.0",
    "io.reactivex.rxjava2" % "rxjava" % "2.1.2"
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

lazy val `common-api` = (project in file("common-api"))
  .settings(commonSettings: _*)

lazy val ingestServiceSettings = Seq(
  name := "ingest-service",
  libraryDependencies ++= Seq("com.typesafe.akka" %% "akka-stream-kafka" % akkaStreamKafkaV),
  mainClass in assembly := Some("com.la.platform.ingest.IngestMain")
)

lazy val predictServiceSettings = Seq(
  libraryDependencies ++= Seq("com.typesafe.akka" %% "akka-stream-kafka" % akkaStreamKafkaV)
)

lazy val `predict-service` = (project in file("predict-service"))
  .settings(commonSettings: _*)
  .settings(apacheSparkSettings: _*)
  .settings(predictServiceSettings: _*)
  .dependsOn(classpathDependency(`common-api`))

lazy val `ingest-service` = (project in file("ingest-service"))
  .settings(commonSettings: _*)
  .settings(ingestServiceSettings: _*)
  .dependsOn(classpathDependency(`common-api`))

lazy val `speed-service` = (project in file("speed-service"))
  .settings(commonSettings: _*)
  .settings(predictServiceSettings: _*)
  .dependsOn(classpathDependency(`common-api`))

lazy val `http-services` =
  project.in(file("."))
    .aggregate(`predict-service`, `ingest-service`, `speed-service`)



