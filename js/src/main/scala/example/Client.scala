package example

import org.scalajs.dom.ext.KeyCode
import org.scalajs.dom.raw.{ CanvasRenderingContext2D => Ctx, MessageEvent }
import org.scalajs.dom
import scalatags.JsDom.all._

object Client {

  def main(args: Array[String]): Unit = {

    val devDiv = Presentation.init(dom.window.location.pathname)

    val heading = h1("Good afternoon ScalaDays!").render

    val t = dom.window
    implicit val canCtx = CanvasCtx(t.innerWidth, t.innerHeight)
    devDiv.appendChild(heading)
    devDiv.appendChild(canCtx.canvas)

    case class Point(x: Double, y: Double)

    case class Rect(width: Int, height: Int)  {
      def draw(point: Point, color: Color)(implicit c: CanvasCtx) = {
        val ctx = c.ctx
        ctx.beginPath()
        ctx.moveTo(point.x, point.y)
        ctx.lineTo(point.x + width, point.y)
        ctx.lineTo(point.x + width, point.y + height)
        ctx.lineTo(point.x, point.y + height)
        ctx.closePath()
        color.fill.foreach(ctx.fillStyle = _)
        ctx.fill()
      }
    }
    Rect(100, 100).draw(Point(100, 100), Color.rand())

  }
}
