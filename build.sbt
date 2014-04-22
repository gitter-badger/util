import AddSettings._

import Dependencies._

val files = Seq(file("../settings.sbt"),
  file("../publish.sbt"))

def proj(name: String) = Project(
  s"util-$name",
  file(s"util-$name")
)

lazy val root = project in(file(".")) autoSettings(
  userSettings, allPlugins, defaultSbtFiles
) settings(
  packagedArtifacts := Map.empty
) aggregate(
  argonaut,
  core,
  finagleHttp,
  id,
  json,
  scal,
  twitter,
  zk
)

lazy val argonaut = proj("argonaut") autoSettings(
  userSettings, allPlugins, sbtFiles(files: _*)
) settings (
  libraryDependencies += "io.argonaut" %% "argonaut" % "6.0.3",
  libraryDependencies ++= finagle("core") ++ util("core")
)

lazy val core = proj("core") autoSettings(
  userSettings, allPlugins, sbtFiles(files: _*)
) settings (
  libraryDependencies ++= scalaz("core")
)

lazy val finagleHttp = proj("finagle-http") autoSettings(
  userSettings, allPlugins, sbtFiles(files: _*)
) settings (
  libraryDependencies ++= scalaz("core") ++ finagle("http")
)

lazy val id = proj("id") autoSettings(
  userSettings, allPlugins, sbtFiles(files: _*)
) settings (
  libraryDependencies ++= finagle("core") ++ util("logging")
)

lazy val json = proj("json") autoSettings(
  userSettings, allPlugins, sbtFiles(files: _*)
) settings (
  libraryDependencies ++= finagle("core") ++ json4s ++
    util("core") ++ scalaz("core")
) dependsOn(core)

lazy val scal = proj("scalendar") autoSettings(
  userSettings, allPlugins, sbtFiles(files: _*)
) settings (
  libraryDependencies ++= json4s ++
    scalaz("core") :+ scalendar
)

lazy val twitter = proj("twitter") autoSettings(
  userSettings, allPlugins, sbtFiles(files: _*)
) settings (
  libraryDependencies ++= util("core") ++ scalaz("core")
)

lazy val zk = proj("zk") autoSettings(
  userSettings, allPlugins, sbtFiles(files: _*)
) settings (
  libraryDependencies ++= util("zk") ++ scalaz("core")
)
