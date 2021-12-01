import Dependencies.Libraries

name := "trio"

organization := "com.trio"

version := "0.3"

val scalaVersion = "2.13.7"

// Required for Heroku
enablePlugins(JavaAppPackaging)

libraryDependencies ++= {
  Libraries.basicDeps ++ Libraries.akkaDeps ++ Libraries.circeDeps ++ Libraries.testDeps
}

scalacOptions ++= Seq(
  "-unchecked",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-deprecation",
  "-Xfatal-warnings",
  "-encoding",
  "utf8",
)

Test / parallelExecution := false
