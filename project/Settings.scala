import sbt.Keys._
import sbt._
import com.lucidchart.sbt.scalafmt.ScalafmtCorePlugin.autoImport._
import com.typesafe.sbt.SbtNativePackager.Universal
import com.typesafe.sbt.SbtNativePackager.autoImport.{maintainer, packageName}
import com.typesafe.sbt.packager.archetypes.scripts.BashStartScriptPlugin.autoImport.{bashScriptDefines, bashScriptExtraDefines}
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport._

import sbtassembly.AssemblyKeys._

object Settings {

  val sdk8 = "adoptopenjdk/openjdk8:x86_64-ubuntu-jdk8u212-b03-slim"
  val sdk11 = "adoptopenjdk/openjdk11:x86_64-alpine-jdk-11.0.4_11-slim"

  lazy val commonSettings = Seq(
    libraryDependencies ++= Seq(
      Circe.core,
      Circe.generic,
      Circe.parser,
      Akka.http,
      Akka.stream,
      Akka.slf4j,
      Akka.contrib,
      Akka.`akka-http-crice`,
      Logback.classic,
      LogstashLogbackEncoder.encoder,
      Config.core,
      Monix.version,
      MySQLConnectorJava.version,
      Redis.client,
      `Akka-http-session`.core
    ) ++ `doobie-quill`.all,
    scalafmtOnCompile in Compile := true,
    scalafmtTestOnCompile in Compile := true
  )

  lazy val dockerSettings = Seq(
    fork := true,
    dockerBaseImage := sdk8,
    maintainer in Docker := "BambooTuna <bambootuna@gmail.com>",
    dockerUpdateLatest := true,
    dockerUsername := Some("bambootuna"),
    mainClass in (Compile, bashScriptDefines) := Some("com.github.BambooTuna.AuthenticationRouterSupport.sample.Main"),
    packageName in Docker := name.value,
    dockerExposedPorts := Seq(8080)
  )

  lazy val gaeSettings = Seq(
    mainClass in assembly := Some("com.github.BambooTuna.AuthenticationRouterSupport.sample.Main"),
    assemblyJarName in assembly := "app.jar"
  )

  lazy val sbtSettings = Seq(
    organization := "com.github.BambooTuna",
    scalaVersion := "2.12.8",
    version := "1.0.0-SNAPSHOT",
    name := "authenticationroutersupport",
    publishTo := Some(Resolver.file("AuthenticationRouterSupport",file("."))(Patterns(true, Resolver.mavenStyleBasePattern)))
  )

}
