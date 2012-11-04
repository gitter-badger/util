import sbt._
import Keys._

object UtilBuild extends Build {
  val utilVersion = "5.3.13"

  val finagleVersion = "5.3.20"

  val ostrichVersion = "8.2.9"

  val querulousVersion = "3.0.3"

  val scalazVersion = "7.0.0-M3"

  val sharedSettings = Seq(
    version := "0.3.0-SNAPSHOT",
    organization := "org.sazabi",
    scalaVersion := "2.9.2",
    scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation"
    ),
    resolvers ++= Seq(
      Classpaths.typesafeResolver,
      Resolver.url("My github releases", url("http://solar.github.com/ivy2/releases/"))(Resolver.ivyStylePatterns),
      Resolver.url("My github snapshots", url("http://solar.github.com/ivy2/snapshots/"))(Resolver.ivyStylePatterns),
      "twitter" at "http://maven.twttr.com"
    )
  )

  lazy val all = Project(
    "util-all",
    file("."),
    settings = Project.defaultSettings ++ Seq(
      publish := false
    )
  ).aggregate(core, finagleHttp, id, json, querulous, redis, zk)

  // Core utilities
  lazy val core = Project(
    "util-core",
    file("util-core"),
    settings = Project.defaultSettings ++ sharedSettings
  ).settings(
    name := "util-core",
    libraryDependencies ++= Seq(
      "com.github.philcali" %% "scalendar" % "0.1.3" % "compile",
      "com.twitter" % "finagle-core" % finagleVersion % "compile"
    )
  ).dependsOn(json)

  // finagle http
  lazy val finagleHttp = Project(
    "util-finagle-http",
    file("util-finagle-http"),
    settings = Project.defaultSettings ++
      sharedSettings
  ).settings(
    name := "util-finagle-http",
    libraryDependencies ++= Seq(
      "com.twitter" % "finagle-http" % finagleVersion % "compile"
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
      "com.twitter" % "util-logging" % utilVersion % "compile",
      "com.twitter" % "ostrich" % ostrichVersion % "compile"
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
      "net.liftweb" % "lift-json_2.9.1" % "2.4" % "compile",
      "org.scalaz" %% "scalaz-core" % scalazVersion % "compile"
    )
  )

  // Database using querulous
  lazy val querulous = Project(
    "util-querulous",
    file("util-querulous"),
    settings = Project.defaultSettings ++ sharedSettings
  ).settings(
    name := "util-querulous",
    libraryDependencies ++= Seq(
      "com.twitter" % "querulous-core" % querulousVersion % "compile",
      "com.twitter" % "querulous-ostrich4" % querulousVersion % "compile"
    )
  ).dependsOn(core)

  // Redis
  lazy val redis = Project(
    "util-redis",
    file("util-redis"),
    settings = Project.defaultSettings ++ sharedSettings
  ).settings(
    name := "util-redis",
    libraryDependencies ++= Seq(
      "com.twitter" % "finagle-ostrich4" % finagleVersion % "compile",
      "com.twitter" % "finagle-redis_2.9.2" % "5.3.19-solar" % "compile" intransitive()
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
      "com.twitter" % "util-zk" % utilVersion % "compile",
      "org.scalaz" %% "scalaz-core" % scalazVersion % "compile"
    )
  )
}
