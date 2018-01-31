package example

import org.scalajs.dom
import scalatags.JsDom.all._
import shared._

trait Slide
case class SimpleSlide(draw: CanvasCtx => Unit) extends Slide
case class ElementsSlide(draw: Seq[CanvasCtx => Unit]) extends Slide
case class AnimationSlide(animation: (Double, CanvasCtx) => Double => Unit) extends Slide {}

object Presentation {

  def el[T <: dom.raw.HTMLElement] (id: String) = dom.document.getElementById(id).asInstanceOf[T]
  val mainDiv = el[dom.html.Div](Ids.main)
  val presDiv = div(id:="Presentation").render
  val devDiv = div(id:="Development", visibility:="hidden").render
  mainDiv.appendChild(presDiv)
  mainDiv.appendChild(devDiv)

  def url(devMode: Boolean, slide: Int) =
    s"/${ if(devMode) "dev" else "pres"}/$slide"

  def pushUrl(devMode: Boolean, slide: Int) =
    dom.window.history.pushState(null, null, url(devMode, slide))

  def initCb() =
    AppCircuit.subscribe(AppCircuit.zoom(_.devMode)){ dev =>
      if (dev.value) {
        devDiv.style.visibility = "visible"
        presDiv.style.visibility = "hidden"
        pushUrl(dev.value, AppCircuit.zoom(_.slide).value)
      } else {
        devDiv.style.visibility = "hidden"
        presDiv.style.visibility = "visible"
        pushUrl(dev.value, AppCircuit.zoom(_.slide).value)
      }
    }

  import Images._

  var currentAnimation: Double => Unit = null

  val slides : List[Slide] =
    AnimationSlide((t, ctx) => {
      import Images.MyCircle
      def createCircles(txt: String, xOffset: Double, yOffset: Double, shrink: Double, vari: Double) = {
        val letters = txt
        val circleSize = 35.0 * shrink
        val spacing = circleSize * 2.0 + 2.0
        def spacings[A](letters: Seq[A], spacing: Int) = (0 to (letters.size * spacing) by spacing)
        val allLetters = letters zip spacings(letters, spacing.toInt)
        allLetters map { case (letter, xPos) =>
          MyCircle(Point(xOffset + xPos, 100 + yOffset),
            if (letter == ' ') Color("white") else Color.rand,
            letter.toString,
            circleSize,
            ((15.0 - scala.util.Random.nextInt(30).toDouble) * shrink),
            33*shrink, vari)
        }
      }
      def centeredCircles(str: String, yOffset: Double, shrink: Double, vari: Double) =
        createCircles(str,
          (ctx.width - ((35.0 * 2.0 * shrink) + 2.0) * str.size) / 2.0, yOffset, shrink, vari)
      val all =
        centeredCircles("Full Stack", 0, 1.5, 30) ++
          centeredCircles("Scala", 150, 1.5, 30) ++
          centeredCircles("Trond Bjerkestrand", 400, 0.8, 20) ++
      centeredCircles("@tbjerkes", 550, 0.7, 20)
      implicit val canv = ctx
      fullStack(all, t) _
    }) ::
    SimpleSlide(c => backInTheDays()(c.ctx)) ::
  SimpleSlide(implicit c => today()(c.ctx)) ::
  ElementsSlide(drawImage()) ::
  ElementsSlide(Seq((c: CanvasCtx) => brain()(c)) ++ {
    val spaceSep = 80
    val elems = Seq("40% speed reduction", "More errors", "Compilers")
    elems zip (200 to 1000 by spaceSep) map { case (t, sep) =>
      implicit ctx: CanvasCtx => Txt.txt2("- " +t, sep.toDouble, Color("black"))
    }}
  ) ::
  AnimationSlide(Txt.txt _) ::
  ElementsSlide{
    val spaceSep = 80
    val elems = Seq("Akka-http", "Autowire", "BooPickle", "Scalatags")
    val first =
      { implicit ctx: CanvasCtx => Txt.txt(0, ctx)(10000) }
    val zipped = elems zip (400 to 1000 by spaceSep) map { case (t, sep) =>
      implicit ctx: CanvasCtx => Txt.txt2("- " +t, sep.toDouble, Color("red"))
    }
    first +: zipped
  } ::
  ElementsSlide{
    val spaceSep = 80
    val elems = Seq("Typesafety", "Tooling", "Scala", "Refactoring")
    val first =
      { implicit ctx: CanvasCtx => Txt.txt2("Benefits", 100, Color("red")) }
    val zipped = elems zip (200 to 1000 by spaceSep) map { case (t, sep) =>
      implicit ctx: CanvasCtx => Txt.txt2("- " +t, sep.toDouble, Color("red"))
    }
    first +: zipped
  } ::
  ElementsSlide{
    val spaceSep = 80
    val elems = Seq("Slow turnaround", "Finding front-end scala devs?")
    val first =
      { implicit ctx: CanvasCtx => Txt.txt2("Problems", 100, Color("red")) }
    val zipped = elems zip (200 to 1000 by spaceSep) map { case (t, sep) =>
      implicit ctx: CanvasCtx => Txt.txt2("- " +t, sep.toDouble, Color("red"))
    }
    first +: zipped
  } ::
  Nil


  def init(path: String) = {
    val pathReg = """/(.*)/(\d*).*""".r //"""/(.*)/(\d*).*""".r
    val w = dom.window
    val canCtx = CanvasCtx(w.innerWidth, w.innerHeight)
    presDiv.appendChild(canCtx.canvas)
    import org.scalajs.dom
    dom.document.addEventListener("keyup", Keys.mainKeyEvents, false)

    AppCircuit.addProcessor(new RAFBatcher)
    AppCircuit.subscribe(AppCircuit.zoom(_.slide)){ slide =>
      pushUrl(AppCircuit.zoom(_.devMode).value, slide.value)
      draw(slide.value, AppCircuit.zoom(_.time).value)(canCtx)
    }
    AppCircuit.subscribe(AppCircuit.zoom(_.subSlide)){ slide =>
      drawSub(AppCircuit.zoom(_.slide).value, slide.value, AppCircuit.zoom(_.time).value)(canCtx)
    }
    AppCircuit.subscribe(AppCircuit.zoom(_.time)){ x =>
      val t = AppCircuit.zoom(_.animation)
      if (t.value) currentAnimation(x.value)
    }

    initCb()
    path match {
      case pathReg("dev", slide) =>
        AppCircuit.dispatch(SlideDev(slide.toInt))
      case pathReg("pres", slide) =>
        AppCircuit.dispatch(SlidePres(slide.toInt))
      case _ =>
        AppCircuit.dispatch(First)
    }
  }

  def draw(slide: Int, time: Double)(implicit canCtx: CanvasCtx) = {
    implicit val CanvasCtx(_, ctx, canvasWidth, canvasHeight) = canCtx
    ctx.clearRect(0, 0, canvasWidth, canvasHeight)
    slides(slide) match {
      case AnimationSlide(anim) =>
        println(s"Drawing anim: $slide")
        currentAnimation = anim(time, canCtx)
        startAnimation()
      case SimpleSlide(draw) =>
        println(s"Drawing simple: $slide")
        draw(canCtx)
      case ElementsSlide(draws) =>
        println(s"Drawing elemSlide: $slide")
        draws.head(canCtx)
    }
  }

  def drawSub(slide: Int, subSlide: Int, time: Double)(implicit canCtx: CanvasCtx) = {
    implicit val CanvasCtx(_, ctx, canvasWidth, canvasHeight) = canCtx
    slides(slide) match {
      case ElementsSlide(draws) =>
        draws.lift(subSlide).foreach(_(canCtx))
      case _ => ()
    }
  }

  def startAnimation() =
    AppCircuit.dispatch(StartAnimation)

  def stopAnimation() =
    AppCircuit.dispatch(StopAnimation)

}
