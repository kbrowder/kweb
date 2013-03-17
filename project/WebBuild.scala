import sbt._
import sbt.Keys._

object WebBuild extends Build {

  lazy val web = Project(
    id = "web",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name := "web",
      organization := "info.browder",
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.10.1",
      resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases",
      libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.1.1",
      libraryDependencies += "com.typesafe.akka" %% "akka-camel" % "2.1.1",
      libraryDependencies += "org.apache.camel" % "camel-jetty" % "2.10.0",
      
      libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.1.1" % "test",
      libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.0.M5b" % "test"
      //libraryDependencies += "org.seleniumhq.selenium" % "selenium-java" % "2.29.1"
    )
  )
}
