package example

import java.lang.Math
import org.scalajs.dom.ext.{ Color => ExtColor, Image, KeyCode }
import org.scalajs.dom.raw.{ CanvasRenderingContext2D => Ctx, Event, HTMLImageElement, KeyboardEvent }
import scala.concurrent.{ Future, Promise }
import scala.scalajs.js
import autowire._
import boopickle.Default._
import scala.util.{ Success, Failure }
import shared.MyType._
import scala.concurrent.ExecutionContext.Implicits.global
import org.scalajs.dom
import shared._

import diode._

object Client {
  type Action2 = Double => Unit;

  def el[T <: dom.raw.HTMLElement] (id: String) = dom.document.getElementById(id).asInstanceOf[T]

  def main(args: Array[String]): Unit = {

    val source = new dom.EventSource("events")

    source.onmessage = { (event: dom.MessageEvent) =>
      if (event.data.toString.nonEmpty)
        println(event.data)
    }

    println(s"Trying to get time from server ...")

    val mainDiv = el[dom.html.Div](Ids.main)
    val realEstate: (Double, Double) = dom.window.innerWidth -> dom.window.innerHeight

    import scalatags.JsDom._
    import scalatags.JsDom.all._
    import org.scalajs.dom.html

    //Wire[shared.Api].getFromServer().call().onComplete {
    //  case Success(m) =>
    //    val msg = m.msg + " " + m.time
    //    println(msg)
    //    mainDiv.appendChild(div(msg).render)
    //  case Failure(f) =>
    //    println("Failed call" + f)
    //    mainDiv.appendChild(div("Not able to contact server").render)
    //}

    val (canvasWidth, canvasHeight) = (realEstate._1.toInt, realEstate._2.toInt)
    val canv = canvas(width:=canvasWidth, height:=canvasHeight, position:="absolute", top:=0, left:=0, zIndex:="-100").render
    mainDiv.appendChild(canv)

    canv.width = canvasWidth.toInt
    canv.height = canvasHeight.toInt
    implicit val ctx: Ctx = canv.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]

    case class Point(x: Int, y: Int)
    sealed trait Shape

    object Shape {
      implicit val shapeDrawTop = new ShapeDraw[Shape] {
        def draw(start: Point, ctx: Ctx, r: Shape) = ()
      }
    }
    case class Rect(height: Int, width: Int) extends Shape
    case class Triangle(height: Int, width: Int) extends Shape
    case class Circle(radius: Int) extends Shape

    trait ShapeDraw[A <: Shape] {
      def draw(p: Point, ctx: Ctx, shape: A): Unit
    }

    def drawShape[A <: Shape](start: Point, color: Color, shape: A)(implicit drawShape: ShapeDraw[A]): Unit = {
      color.applyColor(ctx)
      ctx.beginPath()
      ctx.moveTo(start.x, start.y)
      drawShape.draw(start, ctx, shape)
      ctx.closePath()
      ctx.stroke()
      ctx.fill()
    }

    object Rect {
      implicit val shapeDrawRect = new ShapeDraw[Rect] {
        def draw(start: Point, ctx: Ctx, r: Rect) = {
          ctx.lineTo(start.x + r.width, start.y)
          ctx.lineTo(start.x + r.width, start.y + r.height)
          ctx.lineTo(start.x, start.y + r.height)
        }
      }
    }

    object Triangle {
      implicit val shapeDrawTri = new ShapeDraw[Triangle] {
        def draw(start: Point, ctx: Ctx, t: Triangle) = {
          ctx.lineTo(start.x + t.width, start.y)
            ctx.lineTo(start.x + (t.width / 2), start.y - t.height)
        }
      }
    }

    object Circle {
      implicit val shapeDrawCircle = new ShapeDraw[Circle] {
        def draw(start: Point, ctx: Ctx, c: Circle) = {
          ctx.arc(start.x, start.y, c.radius, 0, 2*Math.PI)
        }
      }
    }


    //drawShape(Point(0,0), ExtColor.Magenta, Rect(29,49))
    //drawShape(Point(40,canvasHeight-60), ExtColor.Magenta, Rect(29,49))
    //drawRect(Point(40,40), Rect(30,30), ExtColor.Green)
    //drawTriangle(Point(200,200), Triangle(100,50), ExtColor.Blue)
    //ctx.strokeText("Full Stack", 20, 20, 200)

    case class ShapeAnimation[A<:Shape](shape: A, x: Int, age: Int, color: Color)(implicit val shapeDraw : ShapeDraw[A]) {
      def draw(t: Int) = drawShape(Point(x, canvasHeight + age - t), color, shape)
    }

    var objects = List[ShapeAnimation[_]]()

    var currTime = 0
    addObject(Rect(60, 500), Color("pink"))

    def addObject[A <: Shape : ShapeDraw](s: A, c: Color) =
      objects = objects :+ ShapeAnimation[A](s, scala.util.Random.nextInt(canvasWidth), currTime, c)

    def loop(delta: Double = 0): Unit = {
      ctx.clearRect(0, 0, canvasWidth, canvasHeight)
      objects.foreach(_.draw(currTime))
        currTime += 1
        dom.window.requestAnimationFrame(loop)
    }

    //loop()

    import SvgUtil._
    import Images._
    //drawImage(ctx, canvasWidth, canvasHeight)

    //var exit: Promise[Unit] = Promise[Unit]()


    def startAnimation() =
      AppCircuit.dispatch(StartAnimation)

    def stopAnimation() =
      AppCircuit.dispatch(StopAnimation)

    def simpleAnimation(t: Double) = {
      ctx.save()
      ctx.clearRect(0, 0, canvasWidth, canvasHeight)
      ctx.beginPath()
      ctx.moveTo(t%200, 200)
      ctx.lineTo(20,40)
      ctx.lineTo(400,400)
      ctx.closePath()
      ctx.stroke()
      ctx.restore()
    }

    def simpleAnimation2(t: Double) = {
      ctx.save()
      ctx.clearRect(0, 0, canvasWidth, canvasHeight)
      ctx.beginPath()
      ctx.moveTo(t%200, t%200)
      ctx.lineTo(20,40)
      ctx.lineTo(t%400,400)
      ctx.closePath()
      ctx.strokeStyle = "red"
      ctx.stroke()
      ctx.restore()
    }

    var currentAnimation: Double => Unit = simpleAnimation

    trait Slide
    case class SimpleSlide() extends Slide
    case class ElementsSlide() extends Slide
    case class AnimationSlide(start: Double) extends Slide

    val slides = List[Action2](
      t => {
        currentAnimation = Txt.txt(t)
        startAnimation()
      },
      t => {
        currentAnimation = simpleAnimation2
        startAnimation()
      },
      t => today(),
      t => backInTheDays(),
      t => drawImage(canvasWidth, canvasHeight)
      //}
    )

    def draw(slide: Int, time: Double) = {
      //exit.success(())
      println(s"draw($slide, $time)")
      ctx.clearRect(0, 0, canvasWidth, canvasHeight)
      //exit = Promise[Unit]()
      slides(slide)(time)
    }


    AppCircuit.addProcessor(new RAFBatcher)
    AppCircuit.subscribe(AppCircuit.zoom(_.slide)){ slide =>
      val t: ModelR[PresentationState, Double] = AppCircuit.zoom(_.time)
      draw(slide.value, t.value)
    }
    AppCircuit.subscribe(AppCircuit.zoom(_.time)){x =>
      val t = AppCircuit.zoom(_.animation)
      if (t.value) currentAnimation(x.value)
    }
    AppCircuit.dispatch(First)

    val keyUp = "keyup"

    dom.document.addEventListener(keyUp, Keys.mainKeyEvents, false)


    val inputF = input().render
    inputF.onkeyup = { event =>
      if(event.keyCode == KeyCode.Enter) {
        parseInput(inputF.value)
      }
    }

    import scala.util.Random.nextInt

    def randomPoint() =
      Point(nextInt(canvasWidth), nextInt(canvasHeight))

    def parseInput(fieldValue: String): Unit = {
      val circleR = """circle (\d*) (.*)""".r
      val rectangleR = """rect (\d*) (\d*) (.*)""".r
      val triangleR = """tri (\d*) (\d*) (.*)""".r
      fieldValue match {
        case circleR(rad, color) =>
          addObject(Circle(rad.toInt), Color(color))
        case rectangleR(width, height, color) =>
          addObject(Rect(width.toInt, height.toInt), Color(color))
        case triangleR(width, height, color) =>
          addObject(Triangle(width.toInt, height.toInt), Color(color))
        case _ =>
          mainDiv.appendChild(div(s"Did not understand $fieldValue").render)
      }
    }

    mainDiv.appendChild(inputF)

  }

}
