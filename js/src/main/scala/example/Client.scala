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

    val d = div(margin:="10px").render
    devDiv.appendChild(d).render

    Wire[shared.Api].getUser().call().onComplete {
      case Success(Person(name, age)) =>
        val msg = if (age < 18)
          h1(s"Sorry $name, only adults allowed here", color:="red")
        else
          h1(s"Welcome $name, please enjoy!", color:="green")
        d.appendChild(msg.render)
      case Failure(f) =>
        println("Failed call" + f)
        d.appendChild(h1("Not able to contact server", color:="red").render)
    }
  }
}
