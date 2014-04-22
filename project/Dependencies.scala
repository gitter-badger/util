import sbt._

object Versions {
  val finagle = "6.13.0"
  val json4s = "3.2.6"
  val scalaz = "7.0.6"
  val scalendar = "0.1.4"
  val util = "6.13.0"
}

object Dependencies {
  def finagle(names: String*) = names map { name =>
    "com.twitter" %% s"finagle-$name" % "6.13.0"
  }

  def scalaz(names: String*) = names map { name =>
    "org.scalaz" %% s"scalaz-$name" % "7.0.6"
  }

  def util(names: String*) = names map {
    case "zk" =>
      ("com.twitter" %% "util-zk" % "6.13.0")
        .exclude("com.sun.jdmk", "jmxtools")
        .exclude("com.sun.jmx", "jmxri")
        .exclude("javax.jms", "jms")
    case name => "com.twitter" %% s"util-$name" % "6.13.0"
  }

  val json4s = Seq(
    "org.json4s" %% "json4s-jackson" % "3.2.6",
    "org.json4s" %% "json4s-scalaz" % "3.2.6")

  val scalendar =
    "com.github.philcali" %% "scalendar" % "0.1.4"
}
