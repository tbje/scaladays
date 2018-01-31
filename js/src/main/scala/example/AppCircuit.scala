package example
import diode._


case class AppState(slide: Int = 0, interaction: Int = 0)

case object First extends Action
case object Next extends Action
case object Prev extends Action
case object ToggleMode extends Action
case class SlideDev(x: Int) extends Action
case class SlidePres(x: Int) extends Action
case object UpdateAnimation extends RAFAction
case object StartAnimation extends RAFAction
case object StopAnimation extends RAFAction
case class PresentationState(slide: Int, subSlide: Int, time: Double, animation: Boolean, devMode: Boolean)

import scala.concurrent.ExecutionContext.Implicits.global

import org.scalajs.dom

object AppCircuit extends Circuit[PresentationState] {

  def initialModel = PresentationState(1, 0, dom.window.performance.now(), false, false)

  def hasMore(s: Slide, el: Int) = s match {
    case ElementsSlide(draws) if el + 1 < draws.size => true
    case _ => false
  }

  val slideHandler = new ActionHandler(zoomRW[PresentationState](x=>x)((x,y)=>y)) {
    override def handle = {
      case Next if hasMore(Presentation.slides(value.slide), value.subSlide) =>
        updated(value.copy(subSlide = value.subSlide + 1, animation = false))
      case Next if value.animation =>
        println("next anim")
        updated(value.copy(animation = false), Effect.action(Next))
      case Next if Presentation.slides.isDefinedAt(value.slide + 1) =>
        println("next slide")
        updated(value.copy(slide = value.slide + 1, subSlide = 0, animation = false))
      case Next =>
        noChange
      case First =>
        updated(value.copy(slide = 0, animation = false, subSlide = 0))
      case SlideDev(x) if value.animation =>
        println("here")
        updated(value.copy(slide = x, animation = false, devMode = true), Effect.action(SlideDev(x)))
      case SlideDev(x) =>
        println("here2")
        updated(value.copy(slide = x, animation = false, devMode = true))
      case SlidePres(x) =>
        println("here3")
        updated(value.copy(slide = x, animation = false, devMode = false))
      case Prev if value.animation =>
        updated(value.copy(animation = false), Effect.action(Prev))
      case Prev if value.slide > 0 => updated(value.copy(slide = value.slide - 1, subSlide = 0))
      case Prev => noChange
      case ToggleMode => updated(value.copy(devMode = !value.devMode))
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
