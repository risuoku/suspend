import AssemblyKeys._ //一番最初の行に書く

name := "gateway"

version := "1.0"

scalaVersion := "2.10.3"

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies ++= Seq(
  "net.debasishg" % "sjson_2.9.1" % "0.15",
  "org.scalatra" % "scalatra-json_2.10" % "2.3.0",
  "org.json4s" %% "json4s-native" % "3.2.10",
  "org.json4s" %% "json4s-jackson" % "3.2.10",
  "org.scala-lang" % "scala-swing" % "2.10+",
//  "org.specs2" % "specs2_2.10" % "2.4.2-scalaz-7.0.6",
  "com.typesafe" % "config" % "1.2.1"
)

//libraryDependencies += "org.scalatest" %% "scalatest" % "1.9.1" % "test"

assemblySettings    //これを追加しますん

test in assembly := {}
