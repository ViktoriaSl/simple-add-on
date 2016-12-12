// Comment to get more information during initialization
logLevel := Level.Warn

//credentials := Seq(Credentials(Path.userHome / ".ivy2" / ".credentials"))

resolvers ++= Seq(
  "atlassian-proxy-internal" at "https://m2proxy.atlassian.com/content/groups/internal/"
 ,"atlassian-proxy-public" at "https://m2proxy.atlassian.com/content/groups/public/"
 ,"atlassian-maven-public" at "http://maven.atlassian.com/content/groups/public/"
 ,"sbt-idea-repo" at "http://mpeltonen.github.com/maven/"
 ,"typesafe-maven" at "http://repo.typesafe.com/typesafe/releases/"
 ,"scct-github-repository" at "http://mtkopone.github.com/scct/maven-repo"
 ,Resolver.url("artifactory", url("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases"))(Resolver.ivyStylePatterns)
)

// re-initialize full resolvers so the resolvers are used after local but before default external (see http://harrah.github.com/xsbt/latest/sxr/Defaults.scala.html#319732)
fullResolvers <<= (projectResolver, externalResolvers, sbtPlugin, sbtResolver) map { (pr,er,isPlugin,sr) =>
  val base = pr +: er
  if(isPlugin) sr +: base else base
}

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.3.4")

addSbtPlugin("com.github.scct" % "sbt-scct" % "0.2")

addSbtPlugin("org.ensime" % "ensime-sbt-cmd" % "0.1.2")

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.5.2")

//addSbtPlugin("com.typesafe.sbt" % "sbt-scalariform" % "1.2.1")

// Visualise all transitive dependencies
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.7.4")

scalacOptions ++= Seq("-deprecation")
