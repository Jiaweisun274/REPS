ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.14"
libraryDependencies += "org.scalaj" %% "scalaj-http" % "2.4.2"

lazy val root = (project in file("."))
  .settings(
    name := "REPS"

  )
