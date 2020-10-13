import sbt._

object Dependencies {

  val projectDependencies = Seq (
    "com.twilio.sdk" % "twilio" % "7.51.0",
    "com.amazonaws" % "aws-java-sdk" % "1.11.797",
    "com.github.ben-manes.caffeine" % "caffeine" % "2.8.4",
    "commons-io" % "commons-io" % "2.7",
    "com.google.cloud" % "google-cloud-storage" % "1.113.1"
  )

}
