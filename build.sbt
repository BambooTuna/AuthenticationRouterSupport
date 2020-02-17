import Settings._

lazy val boot = (project in file("boot"))
  .enablePlugins(JavaAppPackaging, AshScriptPlugin, DockerPlugin)
  .settings(sbtSettings)
  .settings(commonSettings)
  .settings(dockerSettings)

lazy val root =
  (project in file("."))
    .aggregate(boot)
    .settings(sbtSettings)
