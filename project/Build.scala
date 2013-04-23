import sbt._
import Keys._

import com.typesafe.sbt.SbtPgp.PgpKeys._

object UtilBuild extends Build {
  val utilVersion = "6.3.0"

  val finagleVersion = "6.3.0"

  val ostrichVersion = "9.1.0"

  val scalazVersion = "7.0.0"

  val json4sVersion = "3.2.4"

  val sharedSettings = Seq(
    version := "0.7.0-SNAPSHOT",
    organization := "org.sazabi",
    scalaVersion := "2.10.1",
    scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-feature"
    ),
    libraryDependencies ++= Seq(
      "org.specs2" %% "specs2" % "1.14" % "test"
    ),
    useGpg := true,
    publishMavenStyle := true,
    publishTo <<= version { (v: String) =>
      val nexus = "https://oss.sonatype.org/"
      if (v.trim.endsWith("SNAPSHOT"))
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => false },
    pomExtra := (
      <url>https://github.com/solar/util</url>
      <licenses>
        <license>
          <name>Apache 2</name>
          <url>http://www.apache.org/licenses/LICENSE-2.0.txt"</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:solar/util.git</url>
        <connection>scm:git:git@github.com:solar/util.git</connection>
      </scm>
      <developers>
        <developer>
          <id>solar</id>
          <name>Shinpei Okamura</name>
          <url>https://github.com/solar</url>
        </developer>
      </developers>)
  )

  lazy val all = Project(
    "util-all",
    file("."),
    settings = Project.defaultSettings ++ sharedSettings ++ Seq(
      publish := {},
      publishLocal := {},
      publishSigned := {}
    )
  ).aggregate(codec, core, finagleHttp, id, json, netty, redis, twitter, zk)

  // Codecs
  lazy val codec = Project(
    "util-codec",
    file("util-codec"),
    settings = Project.defaultSettings ++ sharedSettings
  ).settings(
    name := "util-codec",
    libraryDependencies ++= Seq(
      "com.twitter" %% "util-core" % utilVersion % "compile",
      "com.twitter" %% "util-codec" % utilVersion % "compile",
      "org.scalaz" %% "scalaz-core" % scalazVersion % "compile"
    )
  )

  // Core utilities
  lazy val core = Project(
    "util-core",
    file("util-core"),
    settings = Project.defaultSettings ++ sharedSettings
  ).settings(
    name := "util-core",
    libraryDependencies ++= Seq(
      "com.github.philcali" %% "scalendar" % "0.1.4" % "compile",
      "org.json4s" %% "json4s-native" % json4sVersion % "compile",
      "org.json4s" %% "json4s-scalaz" % json4sVersion % "compile",
      "org.scalaz" %% "scalaz-core" % scalazVersion % "compile"
    )
  )

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
      "com.twitter" %% "util-logging" % utilVersion % "compile",
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
      "com.twitter" %% "util-core" % utilVersion % "compile",
      "org.json4s" %% "json4s-native" % json4sVersion % "compile",
      "org.json4s" %% "json4s-scalaz" % json4sVersion % "compile",
      "org.scalaz" %% "scalaz-core" % scalazVersion % "compile"
    )
  )

  // netty
  lazy val netty = Project(
    "util-netty",
    file("util-netty"),
    settings = Project.defaultSettings ++ sharedSettings
  ).settings(
    name := "util-netty",
    libraryDependencies ++= Seq(
      "io.netty" % "netty" % "3.5.12.Final" % "compile"
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
      "com.twitter" %% "finagle-redis" % finagleVersion % "compile",
      "com.twitter" %% "util-core" % utilVersion % "compile",
      "org.scalaz" %% "scalaz-core" % scalazVersion % "compile"
    )
  ).dependsOn(twitter)

  // twitter util
  lazy val twitter = Project(
    "util-twitter",
    file("util-twitter"),
    settings = Project.defaultSettings ++ sharedSettings
  ).settings(
    name := "util-twitter",
    libraryDependencies ++= Seq(
      "com.twitter" %% "util-core" % utilVersion % "compile",
      "org.scalaz" %% "scalaz-core" % scalazVersion % "compile"
    )
  )

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
