Nice.javaProject

fatArtifactSettings

name := "bio4j-examples"
organization := "bio4j"
description := "Bio4j examples project"

bucketSuffix := "era7.com"

javaVersion := "1.8"

libraryDependencies ++= Seq(
  "bio4j" % "bio4j-titan" % "0.4.0-SNAPSHOT",
  "com.google.code.gson" % "gson" % "2.2.4"
)


// fat jar assembly settings
mainClass in assembly := Some("com.ohnosequences.bio4j.tools.ExecuteBio4jTool")

assemblyOption in assembly ~= { _.copy(includeScala = false) }

mergeStrategy in assembly ~= { old => {
    case PathList("META-INF", "CHANGES.txt")                     => MergeStrategy.rename
    case PathList("META-INF", "LICENSES.txt")                    => MergeStrategy.rename
    case PathList("org", "apache", "commons", "collections", _*) => MergeStrategy.first
    case x                                                       => old(x)
  }
}
