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

  def initialModel = PresentationState(-1, 0, dom.window.performance.now(), false, false)

  def hasMore(s: Slide, el: Int) = s match {
    case ElementsSlide(draws) if el + 1 < draws.size => true
    case AnimationSlide(_,draws) =>
      val r = if (el < draws.size)  true else false
      println(s"$draws $el $r")
      r
    case _ => false
  }

  val slideHandler = new ActionHandler(zoomRW[PresentationState](x=>x)((x,y)=>y)) {
    override def handle = {
      case UpdateAnimation if !value.animation => noChange
      case UpdateAnimation => effectOnly(Effect.action(UpdateAnimation))
      case StartAnimation =>
        val ts = dom.window.performance.now()
        println(s"Start animation $ts")
        updated(value.copy(animation = true, time = ts), Effect.action(UpdateAnimation))
      case StopAnimation => updated(value.copy(animation = false))
      case x: Action if value.animation & ! x.isInstanceOf[diode.ActionBatch] & !x.isInstanceOf[RAFTimeStamp] =>
        updated(value.copy(animation = false), Effect.action(x))
      case Next if hasMore(Presentation.slides(value.slide), value.subSlide) =>
        updated(value.copy(subSlide = value.subSlide + 1, animation = false))
      case Next if Presentation.slides.isDefinedAt(value.slide + 1) =>
        updated(value.copy(slide = value.slide + 1, subSlide = 0, animation = false))
      case Next =>
        noChange
      case First =>
        updated(value.copy(slide = 0, animation = false, subSlide = 0))
      case SlideDev(x) =>
        println(s"dev $x")
        updated(value.copy(slide = x, animation = false, subSlide = 0, devMode = true))
      case SlidePres(x) =>
        println(s"pres $x")
        updated(value.copy(slide = x, animation = false, subSlide = 0, devMode = false))
      case Prev if value.slide > 0 => updated(value.copy(slide = value.slide - 1, subSlide = 0))
      case Prev => noChange
      case ToggleMode => updated(value.copy(devMode = !value.devMode))
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
