import sbt._

object Versions {
  val finagle = "6.8.1"
  val json4s = "3.2.6"
  val scalaz = "7.0.5"
  val scalendar = "0.1.4"
  val util = "6.8.1"
  val scalaTime = "0.6.0"

  val typelevel = "0.1.5"
}

object Dependencies {
  def finagle(names: String*) = names map { name =>
    "com.twitter" %% s"finagle-$name" % Versions.finagle % "compile"
  }

  def scalaz(names: String*) = names map { name =>
    "org.scalaz" %% s"scalaz-$name" % Versions.scalaz % "compile"
  }

  def util(names: String*) = names map {
    case "zk" =>
      ("com.twitter" %% "util-zk" % Versions.util % "compile")
        .exclude("com.sun.jdmk", "jmxtools")
        .exclude("com.sun.jmx", "jmxri")
        .exclude("javax.jms", "jms")
    case name => "com.twitter" %% s"util-$name" % Versions.util % "compile"
  }

  val json4s = Seq(
    "org.json4s" %% "json4s-jackson" % Versions.json4s % "compile",
    "org.json4s" %% "json4s-scalaz" % Versions.json4s % "compile")

  val scalendar =
    "com.github.philcali" %% "scalendar" % Versions.scalendar % "compile"

  val scalaTime = Seq(
    "com.github.nscala-time" %% "nscala-time" % Versions.scalaTime % "compile",
    "org.typelevel" %% "scalaz-nscala-time" % Versions.typelevel % "compile")
}
