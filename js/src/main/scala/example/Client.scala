package example

import org.scalajs.dom.ext.KeyCode
import org.scalajs.dom.raw.{ CanvasRenderingContext2D => Ctx, MessageEvent }
import org.scalajs.dom
import shared._
import scalatags.JsDom.all._


object Client {

  def main(args: Array[String]): Unit = {
    Presentation.init(dom.window.location.pathname)
    val devDiv = Presentation.devDiv

    val heading = h1("Gueten Abig Zűrich", color:="pink").render

    devDiv.appendChild(heading)

    val list = ul().render

    val source = new dom.EventSource("/events")

    val t = dom.window
    implicit val canCtx = CanvasCtx(t.innerWidth, t.innerHeight)
    devDiv.appendChild(canCtx.canvas)
    source.onmessage = { (e: MessageEvent) =>
      if (e.data != "") {
        list.appendChild(li(e.data.toString).render)
      }
    }

    case class Point(x: Double, y: Double)

    sealed trait Shape {
      def draw(ctx: CanvasCtx, point: Point, color: Color): Unit
      def drawWith(ctx: Ctx, point: Point, color: Color)(func: => Unit) = {
        ctx.beginPath()
        color.applyColor(ctx)
        func
        ctx.closePath()
        ctx.fill()
        ctx.stroke()
      }
    }

    case class Circle(radius: Double) extends Shape {
      def draw(ctx: CanvasCtx, point: Point, color: Color) = {
        drawWith(ctx.ctx, point, color){
          ctx.ctx.arc(point.x, point.y, radius, 0, 2 * math.Pi)
        }
      }
    }

    case class Rect(width: Int, height: Int) extends Shape {
      def draw(ctx: CanvasCtx, point: Point, color: Color ) = {
        drawWith(ctx.ctx, point, color){
          ctx.ctx.moveTo(point.x, point.y)
          ctx.ctx.lineTo(point.x + width, point.y)
          ctx.ctx.lineTo(point.x + width, point.y + height)
          ctx.ctx.lineTo(point.x, point.y + height)
        }
      }
    }

    case class Triangle(width: Int, height: Int) extends Shape {
      def draw(ctx: CanvasCtx, point: Point, color: Color) = {
        drawWith(ctx.ctx, point, color){
          ctx.ctx.moveTo(point.x + (width / 2), point.y)  //top
          ctx.ctx.lineTo(point.x, point.y + height) // bottom left
          ctx.ctx.lineTo(point.x + width, point.y + height) // bottom right
        }
      }
    }

    import scala.util.Random.nextInt


    def randomPoint() =
      Point(nextInt(canCtx.width.toInt).toDouble, nextInt(canCtx.height.toInt).toDouble)

    val CircleReg = """circle (\d*) (.*)""".r
    val RectReg = """rect (\d*) (\d*) (.*)""".r
    val TriReg = """tri (\d*) (\d*) (.*)""".r

    def parseInput(s: String) = s match {
      case CircleReg(rad, color) =>
        Circle(rad.toDouble).draw(canCtx, randomPoint, Color(color))
      case  RectReg(w, h, c) =>
        Rect(w.toInt, h.toInt).draw(canCtx, randomPoint(), Color(c))
      case  TriReg(w, h, c) =>
        Triangle(w.toInt, h.toInt).draw(canCtx, randomPoint(), Color(c))
      case x =>
        println(s"Not able to parse $x")
    }

    val w = 650
    val inputF = input(
      backgroundColor:="pink",
      position:="absolute",
      left:= (canCtx.width-w) / 2,
      width:= w,
      top:= canCtx.height-200,
      height:=200,
      opacity:=0.6,
      outline:="none",
      border:="0px solid",
      fontSize:="3em").render

    inputF.onkeyup = { event =>
      if(event.keyCode == KeyCode.Enter) {
        parseInput(inputF.value)
      }
    }

    devDiv.appendChild(inputF)

  }

}
