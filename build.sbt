lazy val root = project
  .in(file("."))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    scalaVersion := "2.13.7",
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) },
    scalaJSUseMainModuleInitializer := true,
  )
  // Option #1 - pulls in core via %%
  .enablePlugins(Smithy4sCodegenPlugin)
  // Option #2
  .settings(
    libraryDependencies ++= Seq(
      "com.disneystreaming.smithy4s" %%% "smithy4s-core" % "0.10.0"
    )
  )
