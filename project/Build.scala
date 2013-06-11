import sbt._
import sbt.Keys._
import com.typesafe.sbt.SbtScalariform._

object Dependencies {
  import Properties._
  lazy val scalaTest = "org.scalatest" %% "scalatest" % scalaTestVer % "test" withSources() withJavadoc()
  lazy val scalaCheck = "org.scalacheck" %% "scalacheck" % "1.10.0" %  "test" withSources() withJavadoc()
  lazy val h2Db = "com.h2database" % "h2" % h2Version % "test" withSources() withJavadoc()
  lazy val play =  "play" %% "play" % playVersion % "provided" withSources()
}

object ApplicationBuild extends Build {
  import Resolvers._
  import Dependencies._
  import BuildSettings._
  import CodeStyle._

  val commonPatterns = Project(
    id = "f-common-patterns",
    base = file("./modules/commons"),
    settings = buildSettings ++ Seq(resolvers ++= Seq(typesafeReleases, scalaToolsRepo, h2Repo)) ++ Seq(scalacOptions ++= Seq("-feature", "-target:jvm-1.7")) ++
              Seq (libraryDependencies ++= Seq(scalaTest, scalaCheck))
  ).settings(defaultScalariformSettings: _*)
    .settings(scalacOptions ++= Seq("-feature", "-target:jvm-1.7"))
    .settings(ScalariformKeys.preferences := formattingPreferences)
    .settings(resolvers ++= repositories)

  val enterprisePatterns = Project(
    id = "f-enterprise-patterns",
    base = file("./modules/enterprise"),
    settings = buildSettings ++ Seq(resolvers ++= Seq(typesafeReleases, scalaToolsRepo, h2Repo)) ++ Seq(scalacOptions ++= Seq("-feature", "-target:jvm-1.7")) ++
              Seq (libraryDependencies ++= Seq(scalaTest, scalaCheck, h2Db))
  ).settings(defaultScalariformSettings: _*)
    .settings(scalacOptions ++= Seq("-feature", "-target:jvm-1.7"))
    .settings(parallelExecution in Test := false)
    .settings(ScalariformKeys.preferences := formattingPreferences)
    .settings(resolvers ++= repositories)
    .dependsOn(commonPatterns)

  val playModule = Project(
    id = "f-play",
    base = file("./modules/f-play"),
    settings = buildSettings ++ Seq(resolvers ++= Seq(typesafeReleases, scalaToolsRepo, h2Repo)) ++ Seq(scalacOptions ++= Seq("-feature", "-target:jvm-1.7")) ++
      Seq (libraryDependencies ++= Seq(scalaTest, scalaCheck, play))
  ).settings(defaultScalariformSettings: _*)
    .settings(scalacOptions ++= Seq("-feature", "-target:jvm-1.7"))
    .settings(ScalariformKeys.preferences := formattingPreferences)
    .settings(resolvers ++= repositories)
    .dependsOn(enterprisePatterns)


  val allPatterns = Project(
  	id = "f-patterns",
  	base = file(".")
  ) aggregate (commonPatterns, enterprisePatterns, playModule)
}
