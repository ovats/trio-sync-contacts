import sbt._

object Dependencies {

  object Versions {
    val akkaHttpVersion      = "10.2.7"
    val akkaVersion          = "2.6.17"
    val circeVersion         = "0.14.1"
    val akkaHttpCirceVersion = "1.38.2"

    val logbackVersion      = "1.2.7"
    val scalaLoggingVersion = "3.9.4"

    val pureConfigVersion = "0.16.0"

    val scalaTestVersion = "3.2.10"
  }

  object Libraries {
    // Akka
    val akkaHttp    = "com.typesafe.akka" %% "akka-http"                % Versions.akkaHttpVersion
    val akkaActors  = "com.typesafe.akka" %% "akka-actor"               % Versions.akkaVersion
    val akkaStream  = "com.typesafe.akka" %% "akka-stream"              % Versions.akkaVersion
    val akkHttpTest = "com.typesafe.akka" %% "akka-http-testkit"        % Versions.akkaHttpVersion % Test
    val akkaTest    = "com.typesafe.akka" %% "akka-actor-testkit-typed" % Versions.akkaVersion     % Test

    // Circe
    val akkaHttpCirce = "de.heikoseeberger" %% "akka-http-circe"      % Versions.akkaHttpCirceVersion
    val circeCore     = "io.circe"          %% "circe-core"           % Versions.circeVersion
    val circeGeneric  = "io.circe"          %% "circe-generic"        % Versions.circeVersion
    val circeExtras   = "io.circe"          %% "circe-generic-extras" % Versions.circeVersion

    // Logs
    val logback      = "ch.qos.logback"              % "logback-classic" % Versions.logbackVersion % Runtime
    val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging"   % Versions.scalaLoggingVersion

    // PureConfig
    val pureConfig = "com.github.pureconfig" %% "pureconfig" % Versions.pureConfigVersion

    // ScalaTest
    val scalaTest         = "org.scalatest" %% "scalatest"                % Versions.scalaTestVersion % "test"
    val scalaTestFlatSpec = "org.scalatest" %% "scalatest-flatspec"       % Versions.scalaTestVersion % "test"
    val scalaTestMatchers = "org.scalatest" %% "scalatest-shouldmatchers" % Versions.scalaTestVersion % "test"

    val akkaDeps = Seq(
      akkaActors,
      akkaHttp,
      akkaStream,
      akkaHttpCirce,
    )

    val circeDeps = Seq(
      circeCore,
      circeGeneric,
      circeExtras,
    )

    val basicDeps = Seq(
      pureConfig,
      logback,
      scalaLogging,
    )

    val testDeps = Seq(scalaTestFlatSpec, scalaTestMatchers, akkaTest, akkHttpTest)
  }
}
