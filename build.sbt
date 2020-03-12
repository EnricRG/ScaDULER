
name := "ScaDULER"

version := "0.1"

scalaVersion := "2.12.8"

resolvers += Resolver.url("bintray-sbt-plugins", url("http://dl.bintray.com/sbt/sbt-plugin-releases"))(Resolver.ivyStylePatterns)

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.4.20"
libraryDependencies += "org.json4s" % "json4s-jackson_2.12" % "3.6.7"
libraryDependencies += "org.openjfx" % "javafx" % "12" pomOnly()

assemblyJarName in assembly := "ScaDULER.jar"
mainClass in assembly := Some("app.MainApp")
test in assembly := {}

assemblyMergeStrategy in assembly := {
    case PathList("reference.conf") => MergeStrategy.concat
    case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
}