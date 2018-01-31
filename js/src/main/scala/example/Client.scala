package example

import org.scalajs.dom.ext.KeyCode
import org.scalajs.dom.raw.{ CanvasRenderingContext2D => Ctx, MessageEvent }
import org.scalajs.dom
import scalatags.JsDom.all._

object Client {

  def main(args: Array[String]): Unit = {

    val devDiv = Presentation.init(dom.window.location.pathname)

    val heading = h1("Good afternoon ScalaDays!").render

    devDiv.appendChild(heading)

    val t = dom.window
    implicit val canCtx = CanvasCtx(t.innerWidth, t.innerHeight)
    devDiv.appendChild(canCtx.canvas)

    case class Point(x: Double, y: Double)

    sealed trait Shape {
      def draw(point: Point, color: Color)(implicit ctx: CanvasCtx): Unit
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
      def draw(point: Point, color: Color)(implicit ctx: CanvasCtx) = {
        drawWith(ctx.ctx, point, color){
          ctx.ctx.arc(point.x, point.y, radius, 0, 2 * math.Pi)
        }
      }
    }

    case class Rect(width: Int, height: Int) extends Shape {
      def draw(point: Point, color: Color)(implicit ctx: CanvasCtx) = {
        drawWith(ctx.ctx, point, color){
          ctx.ctx.moveTo(point.x, point.y)
          ctx.ctx.lineTo(point.x + width, point.y)
          ctx.ctx.lineTo(point.x + width, point.y + height)
          ctx.ctx.lineTo(point.x, point.y + height)
        }
      }
    }

    case class Triangle(width: Int, height: Int) extends Shape {
      def draw(point: Point, color: Color)(implicit ctx: CanvasCtx) = {
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

    Triangle(300, 200).draw(randomPoint(), Color.rand)
    Circle(100).draw(randomPoint(), Color.rand)
    Rect(100, 50).draw(randomPoint(), Color.rand)

  }
}
