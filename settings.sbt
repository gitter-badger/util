import sbt._

version := "0.11.0-SNAPSHOT"

organization := "org.sazabi"

scalaVersion := "2.10.3"

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-feature")

libraryDependencies += "org.scalatest" %% "scalatest" % "2.0" % "test"
