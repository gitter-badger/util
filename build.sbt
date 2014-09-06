import AddSettings._

val files = Seq(file("../settings.sbt"),
  file("../publish.sbt"))

def proj(name: String) = Project(
  s"util-$name",
  file(s"util-$name")
)

lazy val root = project in(file(".")) settingSets(
  autoPlugins, buildScalaFiles, userSettings, defaultSbtFiles
) settings(
  packagedArtifacts := Map.empty
) aggregate(
  argonaut,
  core,
  finagleHttp,
  id,
  twitter,
  zk
)

lazy val argonaut = proj("argonaut") settingSets(
  autoPlugins, buildScalaFiles, userSettings, sbtFiles(files: _*)
) settings (
  libraryDependencies += "io.argonaut" %% "argonaut" % "6.1-M4",
  libraryDependencies ++= finagle("core") ++ util("core")
)

lazy val core = proj("core") settingSets(
  autoPlugins, buildScalaFiles, userSettings, sbtFiles(files: _*)
) settings (
  libraryDependencies ++= scalaz("core")
)

lazy val crypto = proj("crypto") settingSets(
  autoPlugins, buildScalaFiles, userSettings, sbtFiles(files: _*)
) settings (
  libraryDependencies ++= Seq(
    "org.bouncycastle" % "bcprov-jdk15on" % "1.50",
    "org.jasypt" % "jasypt" % "1.9.2" % "compile" classifier "lite")
)

lazy val finagleHttp = proj("finagle-http") settingSets(
  autoPlugins, buildScalaFiles, userSettings, sbtFiles(files: _*)
) settings (
  libraryDependencies ++= scalaz("core") ++ finagle("http")
)

lazy val id = proj("id") settingSets(
  autoPlugins, buildScalaFiles, userSettings, sbtFiles(files: _*)
) settings (
  libraryDependencies ++= finagle("core") ++ util("logging")
)

lazy val twitter = proj("twitter") settingSets(
  autoPlugins, buildScalaFiles, userSettings, sbtFiles(files: _*)
) settings (
  libraryDependencies ++= util("core") ++ scalaz("core")
)

lazy val zk = proj("zk").settingSets(
  autoPlugins, buildScalaFiles, userSettings, sbtFiles(files: _*)
).settings (
  libraryDependencies ++= util("zk") ++ scalaz("core")
)

def finagle(names: String*) = names map { name =>
  "com.twitter" %% s"finagle-$name" % "6.20.0"
}

def scalaz(names: String*) = names map { name =>
  "org.scalaz" %% s"scalaz-$name" % "7.1.0"
}

def util(names: String*) = names map {
  case "zk" =>
    ("com.twitter" %% "util-zk" % "6.19.0")
      .exclude("com.sun.jdmk", "jmxtools")
      .exclude("com.sun.jmx", "jmxri")
      .exclude("javax.jms", "jms")
  case name => "com.twitter" %% s"util-$name" % "6.19.0"
}
