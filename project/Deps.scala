object Version {
  val akka = "10.0.10"
  val autowire = "0.2.6"
  val boopickle = "1.2.6"
  val monocle = "1.4.0"
  val scala = "2.12.3"
  val scalajsDom = "0.9.3"
  val scalajsScripts = "1.1.1"
  val scalatags = "0.6.7"
  val twitter4s = "5.1"
  val diode = "1.1.2"
}

object Deps {
  import sbt._
  import org.scalajs.sbtplugin.ScalaJSPlugin
  import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._

  val shared = Def.setting(
    "com.lihaoyi" %%% "autowire" % Version.autowire ::
    "com.lihaoyi" %%% "scalatags" % Version.scalatags  ::
    "com.github.julien-truffaut" %%%  "monocle-core"  % Version.monocle ::
    "com.github.julien-truffaut" %%%  "monocle-macro" % Version.monocle ::
    "io.suzaku" %%% "boopickle" % Version.boopickle ::
    "com.danielasfregola" %% "twitter4s" % Version.twitter4s ::
      Nil)

  val server = Def.setting(shared.value ++ (
    "com.typesafe.akka" %%% "akka-http" % Version.akka ::
    "com.vmunier" %%% "scalajs-scripts" % Version.scalajsScripts ::
      Nil))

  val client = Def.setting(shared.value ++ (
    "io.suzaku" %%% "diode" % Version.diode ::
    "org.scala-js" %%% "scalajs-dom" % Version.scalajsDom ::
    Nil))

}
