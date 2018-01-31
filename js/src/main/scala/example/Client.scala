package example

import org.scalajs.dom.ext.KeyCode
import org.scalajs.dom.raw.{ CanvasRenderingContext2D => Ctx, MessageEvent }
import org.scalajs.dom
import scalatags.JsDom.all._
import scala.util._
import autowire._
import boopickle.Default._
import shared.MyType._
import concurrent.ExecutionContext.Implicits.global

object Client {

  def main(args: Array[String]): Unit = {

    val devDiv = Presentation.init(dom.window.location.pathname)

    println(s"Trying to get time from server ...")

    Wire[shared.Api].getFromServer().call().onComplete {
      case Success(m) =>
        val msg = m.msg + " " + m.time
        println(msg)
        devDiv.textContent = msg
      case Failure(f) =>
        println("Failed call" + f)
        devDiv.textContent = "Not able to contact server"
    }
  }

}
