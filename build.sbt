name := "TicTacToe"

version := "1.0"

scalaVersion := "2.13.3"

libraryDependencies ++= Seq(
  "org.scalafx" % "scalafx_2.13" % "16.0.0-R25",
  "com.typesafe.akka" %% "akka-actor-typed" % "2.6.10",
  "com.typesafe.akka" %% "akka-cluster-typed" % "2.6.10"
)


lazy val root = (project in file("."))
  .settings(
    name := "TicTacToeGame"
  )
