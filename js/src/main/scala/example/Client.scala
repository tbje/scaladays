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
import shared.MyType._

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
    def formGroup(t: String) = {
      val theId = t.toLowerCase()
      val i = input(`class`:="form-control", id:=theId).render
      val err = div(`class`:="invalid-feedback", "").render
      val fg = div(`class`:="form-group", label(`for`:=theId, t), i, err).render
      (i, fg, err)
    }
    val success = span(color:="green").render
    val (i, fg, errName) = formGroup("Name")
    val (i2, fg2, errAge) = formGroup("Age")
    val b = button(`class`:="btn btn-primary", marginRight:= 10, `type`:="submit", "Add user").render
    val f = form(width:="70%", marginLeft:=30,
      fg, fg2, b, success).render

    f.onsubmit = { e => {onsubmit; false} }

    val error = span(id:="error", color:="red").render
    devDiv.appendChild(f)
    devDiv.appendChild(error)

    b.onclick = {(e) => submit()}
    def submit() = {
      error.innerHTML = ""
      i.setAttribute("class", "form-control")
      i2.setAttribute("class", "form-control")
      Validation.validatePerson(i.value, i2.value) match {
        case Right(p) =>
          Wire[shared.Api].addUser(p).call().map {
            case Some(p) =>
              success.innerHTML = s"User added: $p!"
              i.value = ""
              i2.value = ""
            case None =>
              success.innerHTML = "Something went wrong"
            case o => println(o)
          }
        case Left((name, age)) =>
          name match {
            case Some(err) =>
              i.setAttribute("class", "is-invalid form-control")
              errName.innerHTML = err
            case _ =>
          }
          age match {
            case Some(err) =>
              i2.setAttribute("class", "is-invalid form-control")
              errAge.innerHTML = err
            case _ =>
          }
      }
    }
//    Wire[shared.Api].getFromServer().call().onComplete {
//      case Success(m) =>
//        val msg = s"Hi I'm ${m.name}"
//        println(msg)
//        devDiv.textContent = msg
//      case Failure(f) =>
//        println("Failed call" + f)
//        devDiv.textContent = "Not able to contact server"
//    }
  }
}
