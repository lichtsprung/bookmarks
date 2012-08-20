import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "bookmarks"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      "net.sourceforge.owlapi" % "owlapi-parent" % "3.3",
      "org.protege" % "protege-editor-owl" % "4.1.0",
      "org.apache.jena" % "jena-core" % "2.7.3"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      resolvers += "Maven Central" at "http://uk.maven.org/maven2/",
      resolvers += "Akka Maven Repository" at "http://repo.akka.io/snapshots"
    )

}
