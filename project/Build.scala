import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "Stranger"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm,
    "mysql" % "mysql-connector-java" % "5.1.10",
    "com.twitter" % "util-core_2.9.1" % "1.12.8",
    "com.twitter" % "util-eval_2.9.1" % "1.12.8"
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    resolvers += "twitter-repo" at "http://maven.twttr.com/"
  )

}
