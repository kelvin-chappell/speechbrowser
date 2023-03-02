import Dependencies._

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(name := "SpeechBrowser", libraryDependencies ++= Seq(polly, jsoup))
