Nice.javaProject

fatArtifactSettings

name := "bio4j-examples"
organization := "bio4j"
description := "Bio4j examples project"

bucketSuffix := "era7.com"

javaVersion := "1.8"

libraryDependencies ++= Seq(
  "bio4j" % "bio4j-titan" % "0.4.0-SNAPSHOT",
  "ohnosequences" % "bioinfo-util" % "1.4.2",
  "com.google.code.gson" % "gson" % "2.2.4"
)

dependencyOverrides ++= Set(
  "commons-codec" % "commons-codec" % "1.7",
  "com.fasterxml.jackson.core" % "jackson-core" % "2.1.2",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.1.2",
  "com.fasterxml.jackson.core" % "jackson-annotations" % "2.1.1",
  "commons-beanutils" % "commons-beanutils" % "1.8.3",
  "commons-beanutils" % "commons-beanutils-core" % "1.8.3"
)


// fat jar assembly settings
mainClass in assembly := Some("com.bio4j.examples.ExecuteBio4jExample")

assemblyOption in assembly ~= { _.copy(includeScala = false) }

mergeStrategy in assembly ~= { old => {
    case PathList("META-INF", "CHANGES.txt")                     => MergeStrategy.rename
    case PathList("META-INF", "LICENSES.txt")                    => MergeStrategy.rename
	case "log4j.properties"                                      => MergeStrategy.filterDistinctLines
    case PathList("org", "apache", "commons", "collections", _*) => MergeStrategy.first
    case x                                                       => old(x)
  }
}
