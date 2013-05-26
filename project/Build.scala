import sbt._
import Keys._
import com.typesafe.sbt.SbtScalariform._

object BuildSettings {
  import Properties._
  lazy val buildSettings = Defaults.defaultSettings ++ Seq (
    organization        := "com.promindis",
    version             := appVer,
    scalaVersion        := scalaVer,
    scalacOptions       := Seq("-unchecked", "-deprecation"),
    ivyValidate         := false
  )
}

object Resolvers {
  lazy val typesafeReleases = "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"
  lazy val scalaToolsRepo = "sonatype-oss-public" at "https://oss.sonatype.org/content/groups/public/"
  lazy val h2Repo =  "H2 repo" at "http://hsql.sourceforge.net/m2-repo/"
}

object TestDependencies {
  import Properties._
  lazy val scalaTest = "org.scalatest" %% "scalatest" % scalaTestVer % "test" withSources() withJavadoc()
  lazy val scalaCheck = "org.scalacheck" %% "scalacheck" % "1.10.0" %  "test" withSources() withJavadoc()
  lazy val h2Db = "com.h2database" % "h2" % h2Version % "test" withSources() withJavadoc()
}

object ApplicationBuild extends Build {
  import Resolvers._
  import TestDependencies._
  import BuildSettings._
  import CodeStyle._

  val commonPatterns = Project(
    id = "f-common-patterns",
    base = file("./modules/commons"),
    settings = buildSettings ++ Seq(resolvers ++= Seq(typesafeReleases, scalaToolsRepo, h2Repo)) ++ Seq(scalacOptions ++= Seq("-feature", "-target:jvm-1.7")) ++
              Seq (libraryDependencies ++= Seq(scalaTest, scalaCheck))
  ).settings(defaultScalariformSettings: _*)
   .settings(ScalariformKeys.preferences := formattingPreferences)
   
  val enterprisePatterns = Project(
    id = "f-enterprise-patterns",
    base = file("./modules/enterprise"),
    settings = buildSettings ++ Seq(resolvers ++= Seq(typesafeReleases, scalaToolsRepo, h2Repo)) ++ Seq(scalacOptions ++= Seq("-feature", "-target:jvm-1.7")) ++
              Seq (libraryDependencies ++= Seq(scalaTest, scalaCheck, h2Db))
  ).settings(defaultScalariformSettings: _*)
   .settings(ScalariformKeys.preferences := formattingPreferences)
   .dependsOn(commonPatterns)
   
  val allPatterns = Project(
  	id = "f-patterns",
  	base = file(".")
  ) aggregate (commonPatterns, enterprisePatterns)
}
