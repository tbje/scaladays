package example

import org.scalajs.dom
import scalatags.JsDom.all._
import shared._

trait Slide
case class SimpleSlide(draw: CanvasCtx => Unit) extends Slide
case class ElementsSlide(draw: Seq[CanvasCtx => Unit]) extends Slide
case class AnimationSlide(animation: (Double, CanvasCtx) => Double => Unit, draw: Seq[CanvasCtx => Unit] = Seq()) extends Slide {}

object Presentation {

  def url(devMode: Boolean, slide: Int) =
    s"/${ if(devMode) "dev" else "pres"}/$slide"

  def pushUrl(devMode: Boolean, slide: Int) =
    dom.window.history.pushState(null, null, url(devMode, slide))

  import Images._
  val white = Color("white")
  val green = Color("green")
  val red = Color("red")
  val blue = Color("#39bad8")
  val sdBlue = Color("#364550")

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
  SimpleSlide(implicit c => norway()) ::
  SimpleSlide(implicit c => backInTheDays()) ::
  SimpleSlide(implicit c => today()) ::
  ElementsSlide(drawImage()) ::
  ElementsSlide(Seq((c: CanvasCtx) => brain()(c)) ++
      RegElemSlide.list(Seq("40% speed reduction", "More errors", "Compilers"), 200, -200)) ::
  AnimationSlide(Txt.txt _, RegElemSlide.list(Seq("Akka-http", "Autowire", "BooPickle", "Scalatags"), 335, 0, Some(white))) ::
  ElementsSlide{ {implicit c: CanvasCtx =>
    val ctx = c.ctx
    ctx.save()
    ctx.fillStyle = sdBlue.fill.get
    ctx.fillRect(0, 0, c.width, c.height)
    ctx.fillStyle = "black"
    ctx.fillRect(170, 150, c.width-250, c.height-250)
    Txt.writeTitle("Anatomy", Some(white))
    Txt.txt3("~ >", 200, 230, Color("green"))
    ctx.restore()
  } :: { implicit c: CanvasCtx =>
    Txt.txt3("ls -1 ~/full-stack", 280, 230, white)
  } :: { implicit c: CanvasCtx =>
    val elems = Seq("build.sbt" -> white, "js" -> blue,  "jvm" -> blue,  "project"-> blue,  "shared"-> blue,  "target" -> blue)
    elems zip ((230+80) to 2000 by 80) foreach { case ((t, co), x) => Txt.txt3(t, 200, x, co)}
  } :: { implicit c: CanvasCtx =>
    val s = 300
    Seq("client", "server", "shared-js", "shared-jvm") zip (s to 2000 by 100) foreach {
      case (t, x) => Txt.txt3(t, 750, x, green)
    }
  } :: { implicit c: CanvasCtx =>
    Images.drawArrow3(Point(730, 290), Point(280, 370), white)
    Images.drawArrow3(Point(730, 385), Point(320, 450), white)
    Images.drawArrow3(Point(730, 495), Point(380, 610), white)
    Images.drawArrow3(Point(730, 585), Point(380, 610), white)
  } :: Nil
  } ::
  ElementsSlide{ {implicit c: CanvasCtx =>
    val ctx = c.ctx
    ctx.save()
    ctx.fillStyle = sdBlue.fill.get
    ctx.fillRect(0, 0, c.width, c.height)
    ctx.fillStyle = "black"
    ctx.fillRect(170, 150, c.width-250, c.height-250)
    Txt.writeTitle("Optimising turn around", Some(white))
    Txt.txt3("sbt>", 200, 230, Color("red"))
    ctx.restore()
  } :: { implicit c: CanvasCtx =>
    Txt.txt3("fastOptJS", 330, 230, white)
  } :: { implicit c: CanvasCtx =>
    Txt.txt3("sbt>", 200, 330, Color("red"))
    Txt.txt3("~fastOptJS", 330, 330, white)
  } :: Nil } ::
  ElementsSlide{ {implicit c: CanvasCtx =>
    val ctx = c.ctx
    ctx.save()
    ctx.fillStyle = sdBlue.fill.get
    ctx.fillRect(0, 0, c.width, c.height)
    ctx.fillStyle = "black"
    ctx.fillRect(170, 150, c.width-250, c.height-250)
    Txt.writeTitle("Optimising the binary", Some(white))
    Txt.txt3("sbt>", 200, 230, Color("red"))
    ctx.restore()
  } :: { implicit c: CanvasCtx =>
    Txt.txt3("fullOptJS", 330, 230, white)
  } :: { implicit c: CanvasCtx =>
    val lines =
      "1,1M js/target/scala-2.12/client-fastopt.js" ::
      "828K js/target/scala-2.12/client-fastopt.js.map" ::
      "   0 js/target/scala-2.12/client-jsdeps.js" ::
      "   0 js/target/scala-2.12/client-jsdeps.min.js" ::
      "260K js/target/scala-2.12/client-opt.js" ::
      Nil
    lines zip ((400) to 2000 by 50) foreach { case (t, x) => Txt.txt4(t, 200, x, Color("white"))}
  } :: Nil } ::
  SimpleSlide(implicit c => console("Typesafe RPC")) ::
  SimpleSlide(implicit c => console("Validation")) ::
  SimpleSlide(implicit c => console("HTML & CSS with Scalatags")) ::
  SimpleSlide(implicit c => console("Playing with canvas")) ::
  SimpleSlide(implicit c => console("Parsing data")) ::
  SimpleSlide(implicit c => console("Fetching some tweets")) ::
  SimpleSlide(implicit c => console("The final piece")) ::
  RegElemSlide("Frameworks", Seq("Udash", "OutWatch", "Diode (Suzaku)", "Scalatags", "Scalajs-react", "Bindings")) ::
  RegElemSlide("Benefits", Seq("FP", "Typesafety", "Tooling", "Scala", "Refactoring", "Code sharing")) ::
  RegElemSlide("Challenges", Seq("Slow turnaround", "Finding front-end scala devs?")) ::
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
          centeredCircles("Thank you", 100, 1.5, 30) ++
          centeredCircles("Trond Bjerkestrand", 400, 0.8, 20) ++
          centeredCircles("@tbjerkes", 550, 0.7, 20)
        implicit val canv = ctx;
        fullStack(all, t, Some("github.com/tbje/scaladays")) _
    }) ::
  Nil

  def console(title: String, prompt: String = "sbt>")(implicit c: CanvasCtx) = {
    val ctx = c.ctx
    ctx.save()
    ctx.fillStyle = sdBlue.fill.get
    ctx.fillRect(0, 0, c.width, c.height)
    ctx.fillStyle = "black"
    ctx.fillRect(170, 150, c.width-250, c.height-250)
    Txt.writeTitle(title, Some(white))
    Txt.txt3(prompt, 200, 230, Color("red"))
    ctx.restore()
  }

  object RegElemSlide {
    val spaceSep = 80
    def list(elems: Seq[String], startY: Int = 200, startX: Int = 0, color: Option[Color] = None) = elems zip (startY to startY + 800 by spaceSep) map { case (t, sep) =>
      implicit ctx: CanvasCtx => SvgUtil.withTranslation(startX, 0){
        implicit ctx => Txt.txt2("- " + t, sep.toDouble, color.getOrElse(sdBlue))
      }
    }
    def apply(heading: String, elems: Seq[String]): ElementsSlide = {
      val first = { implicit ctx: CanvasCtx =>
        SvgUtil.withTranslation(260, 0) { implicit c =>
          Txt.writeTitle(heading, Some(sdBlue))(ctx)
        }
      }
      ElementsSlide(first +: list(elems))
      }
  }

  def init(path: String) = {
    def el[T <: dom.raw.HTMLElement] (id: String) = dom.document.getElementById(id).asInstanceOf[T]
    val mainDiv = el[dom.html.Div](Ids.main)
    val presDiv = div(id:="Presentation").render
    val devDiv  = div(id:="Development", visibility:="hidden").render
    mainDiv.appendChild(presDiv)
    mainDiv.appendChild(devDiv)

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

    path match {
      case pathReg("dev", slide) =>
        AppCircuit.dispatch(SlideDev(slide.toInt))
      case pathReg("pres", slide) =>
        AppCircuit.dispatch(SlidePres(slide.toInt))
      case _ =>
        AppCircuit.dispatch(First)
    }
    devDiv
  }

  def draw(slide: Int, time: Double)(implicit canCtx: CanvasCtx) = {
    implicit val CanvasCtx(_, ctx, canvasWidth, canvasHeight) = canCtx
    ctx.clearRect(0, 0, canvasWidth, canvasHeight)
    slides(slide) match {
      case AnimationSlide(anim, draw) =>
        println(s"Drawing anim: $slide at ${dom.window.performance.now()}")
        //stopAnimation()
        currentAnimation = anim(dom.window.performance.now(), canCtx)
        startAnimation()
      case SimpleSlide(draw) =>
        println(s"Drawing simple: $slide")
        draw(canCtx)
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
      case AnimationSlide(_, draws) =>
        draws.lift(subSlide - 1).foreach(_(canCtx))
      case _ => ()
    }
  }

  def startAnimation() =
    AppCircuit.dispatch(StartAnimation)

  def stopAnimation() =
    AppCircuit.dispatch(StopAnimation)

}
