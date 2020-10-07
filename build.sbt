
name := "ScaDULER"

version := "0.1"

scalaVersion := "2.12.8"

resolvers += Resolver.url("bintray-sbt-plugins", url("http://dl.bintray.com/sbt/sbt-plugin-releases"))(Resolver.ivyStylePatterns)

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.4.20"
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.10.3" % "provided"

assemblyJarName in assembly := "ScaDULER.jar"
mainClass in assembly := Some("app.MainApp")

//adding fxml folder to jar building
Compile / unmanagedResourceDirectories += baseDirectory.value / "resources"

test in assembly := {}

assemblyMergeStrategy in assembly := {
  case "reference.conf" => MergeStrategy.concat //AKKA
  case "module-info.class" => MergeStrategy.last //Jackson
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}