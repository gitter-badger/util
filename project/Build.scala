import sbt._
import Keys._

object UtilBuild extends Build {
  val utilVersion = "6.1.0"

  val finagleVersion = "6.1.1"

  val ostrichVersion = "9.1.0"

  val scalazVersion = "7.0.0-M7"

  val sharedSettings = Seq(
    version := "0.5.0-SNAPSHOT",
    organization := "org.sazabi",
    scalaVersion := "2.10.0",
    scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-feature"
    ),
    resolvers ++= Seq(
      Resolver.url("My github releases", url("http://solar.github.com/ivy2/releases/"))(Resolver.ivyStylePatterns),
      "twitter" at "http://maven.twttr.com"
    ),
    libraryDependencies ++= Seq(
      "org.specs2" %% "specs2" % "1.13" % "test"
    )
  )

  lazy val all = Project(
    "util-all",
    file("."),
    settings = Project.defaultSettings ++ Seq(
      scalaVersion := "2.10.0",
      publish := {},
      publishLocal := {}
    )
  ).aggregate(core, finagleHttp, id, json, redis, zk)

  // Core utilities
  lazy val core = Project(
    "util-core",
    file("util-core"),
    settings = Project.defaultSettings ++ sharedSettings
  ).settings(
    name := "util-core",
    libraryDependencies ++= Seq(
      "com.github.philcali" %% "scalendar" % "0.1.4" % "compile",
      "com.twitter" %% "util-core" % utilVersion % "compile",
      "io.netty" % "netty" % "3.5.9.Final" % "compile"
    )
  ).dependsOn(json)

  // finagle http
  lazy val finagleHttp = Project(
    "util-finagle-http",
    file("util-finagle-http"),
    settings = Project.defaultSettings ++ sharedSettings
  ).settings(
    name := "util-finagle-http",
    libraryDependencies ++= Seq(
      "com.twitter" %% "finagle-http" % finagleVersion % "compile"
    )
  ).dependsOn(core)

  // Id generator
  lazy val id = Project(
    "util-id",
    file("util-id"),
    settings = Project.defaultSettings ++
      sharedSettings
  ).settings(
    name := "util-id",
    libraryDependencies ++= Seq(
      "com.twitter" %% "util-logging" % utilVersion % "compile" from
      "http://maven.twttr.com/com/twitter/util-logging/6.1.0/util-logging-6.1.0.jar",
      "com.twitter" %% "ostrich" % ostrichVersion % "compile"
    )
  )

  // json
  lazy val json = Project(
    "util-json",
    file("util-json"),
    settings = Project.defaultSettings ++ sharedSettings
  ).settings(
    name := "util-json",
    libraryDependencies ++= Seq(
      "org.json4s" %% "json4s-native" % "3.1.0" % "compile",
      "org.scalaz" %% "scalaz-core" % scalazVersion % "compile"
    )
  )

  // Redis
  lazy val redis = Project(
    "util-redis",
    file("util-redis"),
    settings = Project.defaultSettings ++ sharedSettings
  ).settings(
    name := "util-redis",
    libraryDependencies ++= Seq(
      "com.twitter" %% "finagle-ostrich4" % finagleVersion % "compile",
      "com.twitter" %% "finagle-redis" % finagleVersion % "compile"
    )
  ).dependsOn(core)

  // zookeeper
  lazy val zk = Project(
    "util-zk",
    file("util-zk"),
    settings = Project.defaultSettings ++ sharedSettings
  ).settings(
    name := "util-zk",
    libraryDependencies ++= Seq(
      "com.twitter" %% "util-zk" % utilVersion % "compile" excludeAll(
        ExclusionRule(organization = "com.sun.jdmk"),
        ExclusionRule(organization = "com.sun.jmx"),
        ExclusionRule(organization = "javax.jms")),
      "org.scalaz" %% "scalaz-core" % scalazVersion % "compile"
    )
  )
}
