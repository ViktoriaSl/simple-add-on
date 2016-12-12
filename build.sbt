import play.PlayImport._

name := "whoslooking-connect-scala"

version in ThisBuild := "1.0-SNAPSHOT"

scalaVersion := "2.11.7"

javacOptions ++= Seq("-source", "1.7", "-target", "1.7")

scalacOptions ++= Seq("-Xfatal-warnings", "-feature", "-language:higherKinds", "-language:reflectiveCalls", "-target:jvm-1.7")

val acPlayScalaVersion = "latest.release"

val libDependencies = Seq(
  "com.atlassian.connect" %% "ac-play-scala" % acPlayScalaVersion,
  "com.atlassian.connect" %% "ac-play-scala" % acPlayScalaVersion classifier "assets",
  // Add your project dependencies here,
  "com.typesafe.play.plugins" %% "play-plugins-redis" % "2.3.1"
)

val sharedTestDependencies = Seq(
  "junit" % "junit" % "4.12",
  "org.specs2" %% "specs2" % "2.3.13",
  "org.mockito" % "mockito-all" % "1.10.19",
  "com.typesafe.akka" %% "akka-testkit" % "2.3.9"
)

testOptions in Test += Tests.Argument("console", "junitxml")

libraryDependencies ++= libDependencies ++ (sharedTestDependencies map (_ % "test"))

// credentials required when accessing atlassian-proxy-internal
//credentials := Seq(Credentials(Path.userHome / ".ivy2" / ".credentials"))

resolvers ++= Seq(
  Resolver.defaultLocal,
  "atlassian-proxy-internal" at "https://m2proxy.atlassian.com/content/groups/internal/",
  "atlassian-proxy-public" at "https://m2proxy.atlassian.com/content/groups/public/",
  "atlassian-maven-public" at "https://maven.atlassian.com/content/groups/public/",
  "org.sedis Maven Repository" at "http://pk11-scratch.googlecode.com/svn/trunk",
  Classpaths.typesafeReleases,
  DefaultMavenRepository,
  Resolver.sonatypeRepo("releases"),
  Resolver.mavenLocal
)

initialCommands in console := "import scalaz._, Scalaz._"

net.virtualvoid.sbt.graph.Plugin.graphSettings

lazy val main = (project in file(".")).enablePlugins(play.PlayScala, SbtWeb)
