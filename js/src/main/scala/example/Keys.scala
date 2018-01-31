package example

import org.scalajs.dom.ext.{ KeyCode => key }
import org.scalajs.dom.raw.KeyboardEvent
import scala.scalajs.js

object Keyboard {
  def unapply(k: KeyboardEvent) = Some(k.keyCode)
}

object Keys {
  type KeyListener = js.Function1[KeyboardEvent, Unit]
  val mainKeyEvents: KeyListener = {
    case Keyboard(key.Left)  => AppCircuit.dispatch(Prev)
    case Keyboard(key.Right) => AppCircuit.dispatch(Next)
    case e @ Keyboard(key.D) if e.ctrlKey => AppCircuit.dispatch(ToggleMode)
    case _ =>
      ()
  }
}
