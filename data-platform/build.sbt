name := "data-platform"

version := "1.0"

lazy val commonSettings = Seq(
  organization := "com.la.platform.batch",
  version := "1.0",
  scalaVersion := "2.11.8",
  scalacOptions := Seq("-unchecked", "-feature", "-deprecation", "-encoding", "utf8"),
  libraryDependencies ++= Seq(
    "org.apache.spark" %% "spark-core" % "2.0.0",
    "org.apache.spark" %% "spark-sql" % "2.0.0",
    "org.apache.spark" %% "spark-streaming" % "2.0.0",
    "org.apache.spark" %% "spark-mllib" % "2.0.0",
    "org.apache.spark" %% "spark-streaming" % "2.0.0"
  ))

lazy val ingestDataSettings = Seq(
  libraryDependencies ++= Seq(
    "org.apache.spark" %% "spark-streaming-kafka-0-8" % "2.0.0",
    "org.apache.kafka" % "kafka_2.11" % "0.9.0.1",
    "org.apache.kafka" % "kafka-clients" % "0.9.0.1"
  )
)

lazy val ingestData = (project in file("ingest-data"))
  .settings(commonSettings: _*)
  .settings(ingestDataSettings: _*)

lazy val dataPlatform =
  project.in(file("."))
    .aggregate(ingestData)

resolvers += Resolver.mavenLocal