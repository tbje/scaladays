package example

import org.scalajs.dom.ext.KeyCode
import org.scalajs.dom.raw.{ CanvasRenderingContext2D => Ctx, MessageEvent }
import org.scalajs.dom
import scalatags.JsDom.all._
import scala.util._
import autowire._
import boopickle.Default._
import concurrent.ExecutionContext.Implicits.global
import shared._

object Client {

  def main(args: Array[String]): Unit = {

    val devDiv = Presentation.init(dom.window.location.pathname)

    println(s"Trying to get time from server ...")

    Wire[shared.Api].getUser().call().onComplete {
      case Success(Person(name, age)) =>
        val msg = if (age < 18)
          s"Sorry $name, only adults allowed here"
        else
          s"Welcome $name, please enjoy!"
        devDiv.textContent = msg
      case Failure(f) =>
        println("Failed call" + f)
        devDiv.textContent = "Not able to contact server"
    }
  }

}
