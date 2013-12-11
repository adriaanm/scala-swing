import VersionKeys._

// DOUBLETHINK YOUR WAY OUT OF EDITING BELOW (THERE IS NO BELOW)

scalaBinaryVersion := deriveBinaryVersion(scalaVersion.value, snapshotScalaBinaryVersion.value)

// so we don't have to wait for sonatype to synch to maven central when deploying a new module
resolvers += Resolver.sonatypeRepo("releases")

// to allow compiling against snapshot versions of Scala
resolvers += Resolver.sonatypeRepo("snapshots")


// Generate $name.properties to store our version as well as the scala version used to build
resourceGenerators in Compile <+= Def.task {
  val props = new java.util.Properties
  props.put("version.number", version.value)
  props.put("scala.version.number", scalaVersion.value)
  props.put("scala.binary.version.number", scalaBinaryVersion.value)
  val file = (resourceManaged in Compile).value / s"${name.value}.properties"
  IO.write(props, null, file)
  Seq(file)
}


// maven publishing
// DISABLED FOR BINTRAY EXPERIMENTATION -- BRING SNAPSHOT PART back
// publishTo := {
//   val nexus = "https://oss.sonatype.org/"
//   if (version.value.trim.endsWith("SNAPSHOT"))
//     Some("snapshots" at nexus + "content/repositories/snapshots")
//   else
//     Some("releases" at nexus + "service/local/staging/deploy/maven2")
// }

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := (
  <url>http://www.scala-lang.org/</url>
  <inceptionYear>2002</inceptionYear>
  <licenses>
    <license>
        <distribution>repo</distribution>
        <name>BSD 3-Clause</name>
        <url>https://github.com/scala/{name.value}/blob/master/LICENSE.md</url>
    </license>
   </licenses>
  <scm>
    <connection>scm:git:git://github.com/scala/{name.value}.git</connection>
    <url>https://github.com/scala/{name.value}</url>
  </scm>
  <issueManagement>
    <system>JIRA</system>
    <url>https://issues.scala-lang.org/</url>
  </issueManagement>
  <developers>
    <developer>
      <id>epfl</id>
      <name>EPFL</name>
    </developer>
    <developer>
      <id>Typesafe</id>
      <name>Typesafe, Inc.</name>
    </developer>
  </developers>
)

val osgiVersion = version(_.replace('-', '.'))

OsgiKeys.bundleSymbolicName := s"${organization.value}.${name.value}"

OsgiKeys.bundleVersion := osgiVersion.value

// Sources should also have a nice MANIFEST file
packageOptions in packageSrc := Seq(Package.ManifestAttributes(
                      ("Bundle-SymbolicName", s"${organization.value}.${name.value}.source"),
                      ("Bundle-Name", s"${name.value} sources"),
                      ("Bundle-Version", osgiVersion.value),
                      ("Eclipse-SourceBundle", s"""${organization.value}.${name.value};version="${osgiVersion.value}";roots:="."""")
                  ))