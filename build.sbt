import AssemblyKeys._

Nice.javaProject

fatArtifactSettings

name := "examples"
organization := "bio4j"
description := "Bio4j examples project"

bucketSuffix := "era7.com"

javaVersion := "1.8"

// resolvers ++= Seq(
//   "Gephi releases" at "http://nexus.gephi.org/nexus/content/repositories/releases/"
// )

libraryDependencies ++= Seq(
  "bio4j" % "bio4j-titan" % "0.4.0-SNAPSHOT"
  // "org.gephi" % "gephi-toolkit" % "0.8.2" classifier("all") intransitive
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
