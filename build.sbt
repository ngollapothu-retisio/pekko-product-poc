import scala.collection.Seq

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.5"

lazy val `pekko-product-poc` = (project in file("."))
  .enablePlugins(PlayMinimalJava, LauncherJarPlugin,JavaAgent,JavaAppPackaging)
  .settings(common,Dependencies.kamonSettings)
  .aggregate(`pekko-product`)
  .dependsOn(`pekko-product`)

lazy val `pekko-product` = (project in file("pekko-product"))
  .settings(common)
  .settings(
    libraryDependencies ++= Seq(
      lombok,
      guice
    )
  )


val lombok = "org.projectlombok" % "lombok" % "1.18.36"

def common = Seq(
  dockerExposedPorts := Seq(9000, 8558, 9001, 10001),
  dockerBaseImage := "openjdk:11-jre-slim",
  Compile / doc / sources := Seq.empty,
  Compile / javacOptions := Seq("-g", "-encoding", "UTF-8", "-Xlint:unchecked", "-Xlint:deprecation", "-parameters"),
  libraryDependencies ++= Dependencies.dependencies
)
Global / excludeLintKeys += dockerBaseImage
Global / excludeLintKeys += dockerExposedPorts