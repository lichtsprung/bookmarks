import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "bookmarks"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      "net.sourceforge.owlapi" % "owlapi-distribution" % "3.4",
      "org.protege" % "protege-editor-owl" % "4.1.0",
      "org.apache.jena" % "jena-core" % "2.7.3",
      "org.apache.jena" % "jena-arq" % "2.9.3",
      "com.typesafe.akka" % "akka-actor" % "2.0.3",
      "com.typesafe.akka" % "akka-remote" % "2.0.3",
      "org.apache.lucene" % "lucene-parent" % "4.0.0-BETA",
      "org.apache.lucene" % "lucene-core" % "4.0.0-BETA",
      "org.apache.lucene" % "lucene-analyzers-common" % "4.0.0-BETA",
      "edu.uci.ics" % "crawler4j" % "3.4"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      resolvers += "Akka Maven Repository" at "http://repo.akka.io/snapshots"
    )

}
