package example
import org.scalajs.dom.raw.{ CanvasRenderingContext2D => Ctx }

object SvgUtil {

  def withTranslation(x: Double, y: Double)(block: Ctx => Unit)(implicit ctx: Ctx) = {
    ctx.save()
    ctx.translate(x, y)
    block(ctx)
    ctx.restore()
  }

  def withScale(x: Double, y: Double)(block: Ctx => Unit)(implicit ctx: Ctx) = {
    ctx.save()
    ctx.scale(x, y)
    block(ctx)
    ctx.restore()
  }

  def withPath(color: Color)(block: Ctx => Unit)(implicit ctx: Ctx) = {
    val oldFill = ctx.fillStyle
    val oldStrokeStyle = ctx.strokeStyle
    val oldStroke = ctx.lineWidth
    color.fill.foreach(fill => ctx.fillStyle = fill)
    color.stroke.foreach{ stroke =>
      ctx.lineWidth = stroke._1.toDouble
      ctx.strokeStyle = stroke._2
    }
    ctx.beginPath()
    block(ctx)
    ctx.closePath()
    color.fill.foreach{ stroke =>
      ctx.fill()
    }
    color.stroke.foreach{ stroke =>
      ctx.stroke()
    }
    ctx.strokeStyle = oldStrokeStyle
    ctx.lineWidth = oldStroke
    ctx.fillStyle = oldFill
  }
}
