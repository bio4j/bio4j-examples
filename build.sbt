import AssemblyKeys._

Nice.javaProject

Nice.fatArtifactSettings

name := "examples"

description := "examples project"

organization := "ohnosequences"

bucketSuffix := "era7.com"

resolvers ++= Seq(
  "Gephi" at "http://nexus.gephi.org/nexus/content/repositories/releases/"
)

libraryDependencies ++= Seq(
  "ohnosequences" % "bio4j-neo4jdb" % "0.1.0-SNAPSHOT",
  "org.gephi" % "gephi-toolkit" % "0.8.2" classifier("all") intransitive
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
