package example
import diode._


case class AppState(slide: Int = 0, interaction: Int = 0)

case object First extends Action
case object Next extends Action
case object Prev extends Action
case object DevMode extends Action
case object UpdateAnimation extends RAFAction
case object StartAnimation extends RAFAction
case object StopAnimation extends RAFAction
case class PresentationState(slide: Int, subSlide: Int, time: Double, animation: Boolean)

import scala.concurrent.ExecutionContext.Implicits.global

import org.scalajs.dom

object AppCircuit extends Circuit[PresentationState] {
  // define initial value for the application model
  def initialModel = PresentationState(1, 0, dom.window.performance.now(), false)

  // zoom into the model, providing access only to the
  val slideHandler = new ActionHandler(zoomRW[PresentationState](x=>x)((x,y)=>y)) {
    override def handle = {
      case Next if value.animation =>
        println("next anim")
        updated(value.copy(animation = false), Effect.action(Next))
      case Next =>
        println("next init")
        updated(value.copy(slide = value.slide + 1, animation = false))
      case First =>
        updated(value.copy(slide = 0, animation = false))
      case Prev if value.animation =>
        updated(value.copy(animation = false), Effect.action(Prev))
      case Prev => updated(value.copy(slide = value.slide - 1))
      case DevMode => updated(value.copy(slide = 0))
      case UpdateAnimation if !value.animation =>
        noChange
      case StartAnimation => updated(value.copy(animation = true), Effect.action(UpdateAnimation))
      case StopAnimation => updated(value.copy(animation = false))
      case UpdateAnimation =>
        effectOnly(Effect.action(UpdateAnimation))
    }
  }

  val timestampHandler = new ActionHandler(zoomTo(_.time)) {
    override def handle = {
      case RAFTimeStamp(time) =>
        updated(time)
    }
  }

  override val actionHandler: HandlerFunction = composeHandlers(slideHandler, timestampHandler)
}
