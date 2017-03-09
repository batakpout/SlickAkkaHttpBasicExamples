name := "MyProject"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "com.typesafe.slick" % "slick_2.11" % "3.2.0-M1",
  "com.typesafe.akka" %% "akka-stream-experimental" % "1.0",
  "com.typesafe.akka" %% "akka-http-core-experimental" % "1.0",
  "org.postgresql" % "postgresql" % "9.4.1211",
  "org.slf4j" % "slf4j-log4j12" % "1.7.22",
  "com.typesafe.akka" %% "akka-http-experimental" % "1.0",
  "org.json4s" %% "json4s-jackson" % "3.2.11","joda-time" % "joda-time" % "2.8.2"
)

