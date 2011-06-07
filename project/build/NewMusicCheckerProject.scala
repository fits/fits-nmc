import sbt._

class NewMusicCheckerProject(info: ProjectInfo) extends DefaultWebProject(info) {
	val jettyVersion = "8.0.0.M3"
	val scalatraVersion = "2.0.0-SNAPSHOT"
	val specs2Version = "1.4"

	val jettyWebapp = "org.eclipse.jetty" % "jetty-webapp" % jettyVersion % "test"
	//コンパイルに必要
	val servletapi = "javax.servlet" % "servlet-api" % "2.5" % "provided"

	//Scalatra のライブラリ設定
	val scalatra = "org.scalatra" %% "scalatra" % scalatraVersion

	//Specs2
	val specs = "org.specs2" %% "specs2" % specs2Version % "test"
	def specs2Framework = new TestFramework("org.specs2.runner.SpecsFramework")
	override def testFrameworks = super.testFrameworks ++ Seq(specs2Framework)

	//Scalatra のダウンロードサイトの設定
	val sonatypeNexusSnapshots = "Sonatype Nexus Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
	val sonatypeNexusReleases = "Sonatype Nexus Releases" at "https://oss.sonatype.org/content/repositories/releases"

	//GAE for Java SDK 
	lazy val appengineSdkRoot = System.getenv("APPENGINE_SDK_HOME")
	lazy val appengineToolsApiJar = Path.fromFile(appengineSdkRoot) / "lib" / "appengine-tools-api.jar"

	lazy val appcfgUpdate = task {args =>
		runTask(Some("com.google.appengine.tools.admin.AppCfg"), appengineToolsApiJar, args ++ List("update", temporaryWarPath.projectRelativePath)) dependsOn (prepareWebapp)
	}
}
