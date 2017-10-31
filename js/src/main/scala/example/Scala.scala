package exemple
import org.scalajs.dom.raw.{ CanvasRenderingContext2D => Ctx }

object Scala {
    def scalaElem(x: Int)(implicit ctx: Ctx)  = {
      ctx.beginPath()
        ctx.fillStyle = "black"
        ctx.fillRect(25, 75, 5, 5)
        ctx.fillStyle = "red"
        ctx.moveTo(30, x)
        ctx.bezierCurveTo(30, x, 300+30, x-30, 300+30, x-80)
        ctx.lineTo(300+30, x-80+120)
        ctx.bezierCurveTo(300+30, x-80+120, 300+30, x-80+120+50, 30, x+120)
        ctx.lineTo(30, x+120+120)
        ctx.closePath()
        ctx.fillStyle = "red"
        ctx.fill()
    }
    def scalaBack(trans: Int)(implicit ctx: Ctx) = {
      ctx.beginPath()
        val x = 80
        ctx.save()
        ctx.transform( 1, 0, 0, -1, 0, trans)
        ctx.fillStyle = "black"
        ctx.moveTo(30, x)
        ctx.bezierCurveTo(30, x, 300+30, x-30, 300+30, x-80)
        ctx.lineTo(300+30, x-80+120)
        ctx.bezierCurveTo(300+30, x-80+120, 300+30, x-80+120+50, 30, x+120)
        ctx.lineTo(30, x+120+120)
        ctx.closePath()
        ctx.fillStyle = "grey"
        ctx.fill()
        ctx.restore()
    }

    def drawScala(implicit ctx: Ctx) : Unit = {
      scalaBack(280)
      scalaBack(440)
      scalaElem(80)
      scalaElem(240)
      scalaElem(400)
    }
}
