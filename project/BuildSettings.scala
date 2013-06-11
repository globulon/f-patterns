import sbt.Defaults
import sbt.Keys._
import Properties._

object BuildSettings {
  lazy val buildSettings = Defaults.defaultSettings ++ Seq (
    organization        := "com.promindis",
    version             := appVer,
    scalaVersion        := scalaVer,
    scalacOptions       := Seq("-unchecked", "-deprecation"),
    ivyValidate         := false
  )
}
