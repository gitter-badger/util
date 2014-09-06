import sbt._

version := "0.12.0-SNAPSHOT"

organization := "org.sazabi"

scalaVersion := "2.10.4"

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-feature")

libraryDependencies += "org.scalatest" %% "scalatest" % "2.1.6" % "test"

incOptions := incOptions.value.withNameHashing(true)
