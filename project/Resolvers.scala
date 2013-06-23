import sbt._

object Resolvers {
  lazy val typesafeReleases = "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"
  lazy val scalaToolsRepo = "sonatype-oss-public" at "https://oss.sonatype.org/content/groups/public/"
  lazy val h2Repo =  "H2 repo" at "http://hsql.sourceforge.net/m2-repo/"
  lazy val repositories = Seq(typesafeReleases, scalaToolsRepo)
}
