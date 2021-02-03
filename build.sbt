name := "parking-lot"

version := "0.1"

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  "com.google.inject" % "guice" % "4.0",
  "joda-time" % "joda-time" % "2.8.1",
  "org.scalatest" %% "scalatest" % "3.0.0" % "test" withSources()
)