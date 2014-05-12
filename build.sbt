import AddSettings._

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

def finagle(names: String*) = names map { name =>
  "com.twitter" %% s"finagle-$name" % "6.15.0"
}

def scalaz(names: String*) = names map { name =>
  "org.scalaz" %% s"scalaz-$name" % "7.0.6"
}

def util(names: String*) = names map {
  case "zk" =>
    ("com.twitter" %% "util-zk" % "6.15.0")
      .exclude("com.sun.jdmk", "jmxtools")
      .exclude("com.sun.jmx", "jmxri")
      .exclude("javax.jms", "jms")
  case name => "com.twitter" %% s"util-$name" % "6.15.0"
}
