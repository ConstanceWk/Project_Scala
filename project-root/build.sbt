ThisBuild / scalaVersion := "3.3.1"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "edu.efrei"

lazy val root = (project in file("."))
  .settings(
    name := "library-management-system",
    // Configuration de la classe principale
    Compile / mainClass := Some("LibraryServer"),
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.2.17" % Test,
      "org.scalacheck" %% "scalacheck" % "1.17.0" % Test,
      "org.typelevel" %% "cats-core" % "2.10.0",
      "io.circe" %% "circe-core" % "0.14.6",
      "io.circe" %% "circe-generic" % "0.14.6",
      "io.circe" %% "circe-parser" % "0.14.6",
      "com.typesafe.akka" %% "akka-http" % "10.5.0",
      "com.typesafe.akka" %% "akka-stream" % "2.8.0",
      "com.typesafe.akka" %% "akka-actor-typed" % "2.8.0",
      "com.lihaoyi" %% "requests" % "0.8.0" % Test, // pour tests HTTP
      "com.typesafe.akka" %% "akka-http-testkit" % "10.5.0" % Test, // version compatible Scala 3
      "com.typesafe.akka" %% "akka-actor-testkit-typed" % "2.8.0" % Test // pour TestDuration
    ),
    // Configuration de la couverture de code
    coverageEnabled := true,
    coverageMinimumStmtTotal := 80,
    coverageFailOnMinimum := true,
    scalacOptions += "-Xmax-inlines",
    scalacOptions += "64"


  )