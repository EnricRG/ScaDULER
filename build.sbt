name := "ScaDULER"

version := "0.1"

scalaVersion := "2.12.8"

//libraryDependencies += "org.scalafx" %% "scalafx" % "8.0.192-R14"

// Add dependency on ScalaFX library
//libraryDependencies += "org.scalafx" %% "scalafx" % "12.0.1-R17"

resolvers += Resolver.url("bintray-sbt-plugins", url("http://dl.bintray.com/sbt/sbt-plugin-releases"))(Resolver.ivyStylePatterns)

// Determine OS version of JavaFX binaries
lazy val osName = System.getProperty("os.name") match {
  case n if n.startsWith("Linux")   => "linux"
  case n if n.startsWith("Mac")     => "mac"
  case n if n.startsWith("Windows") => "win"
  case _ => throw new Exception("Unknown platform!")
}

lazy val javaFXModules = Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")
libraryDependencies ++= javaFXModules.map( m =>
  "org.openjfx" % s"javafx-$m" % "12.0.1" classifier osName
)

libraryDependencies +=
  "com.typesafe.akka" %% "akka-actor" % "2.4.20"

//addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)

//libraryDependencies += "org.scalafx" %% "scalafxml-core-sfx8" % "0.4"