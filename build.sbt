import ReleaseTransformations._

lazy val summarizerSettings = Seq(
  organization := "com.rklaehn",
  scalaVersion := "2.12.4",
  crossScalaVersions := Seq("2.12.4", "2.11.11"),
  libraryDependencies ++= Seq(
    "com.google.guava" % "guava" % "18.0",
    "org.scalatest" %% "scalatest" % "3.0.1" % "test",
    "org.scalacheck" %% "scalacheck" % "1.13.5" % "test",

    // thyme
    "com.github.ichoran" %% "thyme" % "0.1.2-SNAPSHOT" % "test"
  ),
  scalacOptions ++= Seq(
    "-deprecation",
    "-unchecked",
    "-feature"
  ),
  licenses += ("Apache License, Version 2.0", url("http://www.apache.org/licenses/LICENSE-2.0.txt")),
  homepage := Some(url("http://github.com/rklaehn/sonicreducer")),

  // release stuff
  credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),
  releaseCrossBuild := true,
  releasePublishArtifactsAction := PgpKeys.publishSigned.value,
  publishMavenStyle := true,
  publishArtifact in Test := false,
  pomIncludeRepository := Function.const(false),
  publishTo <<= version { v =>
    val nexus = "https://oss.sonatype.org/"
    if (v.trim.endsWith("SNAPSHOT"))
      Some("Snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("Releases" at nexus + "service/local/staging/deploy/maven2")
  },
  pomExtra :=
    <scm>
      <url>git@github.com:rklaehn/sonicreducer.git</url>
      <connection>scm:git:git@github.com:rklaehn/sonicreducer.git</connection>
    </scm>
    <developers>
      <developer>
        <id>r_k</id>
        <name>R&#xFC;diger Klaehn</name>
        <url>http://github.com/rklaehn/</url>
      </developer>
    </developers>
  ,
  releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    runClean,
    ReleaseStep(action = Command.process("package", _)),
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    ReleaseStep(action = Command.process("publishSigned", _)),
    setNextVersion,
    commitNextVersion,
    ReleaseStep(action = Command.process("sonatypeReleaseAll", _)),
    pushChanges))

lazy val noPublish = Seq(
  publish := {},
  publishLocal := {},
  publishArtifact := false)

lazy val root = project.in(file("."))
  .aggregate(core)
  .settings(name := "root")
  .settings(summarizerSettings: _*)
  .settings(noPublish: _*)

lazy val core = project.in(file("."))
  .settings(name := "persistentsummary")
  .settings(summarizerSettings: _*)
