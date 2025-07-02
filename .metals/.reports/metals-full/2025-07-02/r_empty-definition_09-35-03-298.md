error id: file://<WORKSPACE>/project-root/build.sbt:`<none>`.
file://<WORKSPACE>/project-root/build.sbt
empty definition using pc, found symbol in pc: `<none>`.
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -libraryDependencies.
	 -libraryDependencies#
	 -libraryDependencies().
	 -scala/Predef.libraryDependencies.
	 -scala/Predef.libraryDependencies#
	 -scala/Predef.libraryDependencies().
offset: 224
uri: file://<WORKSPACE>/project-root/build.sbt
text:
```scala
ThisBuild / scalaVersion := "3.3.1"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "edu.efrei"

lazy val root = (project in file("."))
  .settings(
    name := "library-management-system",
    libraryDep@@endencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.2.17" % Test,
      "org.scalacheck" %% "scalacheck" % "1.17.0" % Test,
      "org.typelevel" %% "cats-core" % "2.10.0",
      "io.circe" %% "circe-core" % "0.14.6",
      "io.circe" %% "circe-generic" % "0.14.6",
      "io.circe" %% "circe-parser" % "0.14.6"
    )
  )

```


#### Short summary: 

empty definition using pc, found symbol in pc: `<none>`.