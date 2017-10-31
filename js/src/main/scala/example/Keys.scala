package example

import org.scalajs.dom.ext.{ KeyCode => key }
import org.scalajs.dom.raw.KeyboardEvent
import scala.scalajs.js

object Keyboard {
  def unapply(k: KeyboardEvent) = Some(k.keyCode)
}
import Client.Action2

object Keys {
  type KeyListener = js.Function1[KeyboardEvent, Unit]
  val mainKeyEvents: KeyListener = {
    case Keyboard(key.Left)  => AppCircuit.dispatch(Prev)
    case Keyboard(key.Right) => AppCircuit.dispatch(Next)
    case e @ Keyboard(key.D) if e.ctrlKey => AppCircuit.dispatch(DevMode)
    case _ =>
      ()
  }
}
