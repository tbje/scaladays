package example
import org.scalajs.dom.raw.{ CanvasRenderingContext2D => Ctx }

object SvgUtil {

  def withTranslation(x: Double, y: Double)(block: CanvasCtx => Unit)(implicit c: CanvasCtx) = {
    val ctx = c.ctx
    ctx.save()
    ctx.translate(x, y)
    block(c)
    ctx.restore()
  }

  def withScale(x: Double, y: Double)(block: CanvasCtx => Unit)(implicit c: CanvasCtx) = {
    val ctx = c.ctx
    ctx.save()
    ctx.scale(x, y)
    block(c)
    ctx.restore()
  }

  def withPath(color: Color)(block: Ctx => Unit)(implicit c: CanvasCtx) = {
    val ctx = c.ctx
    ctx.save()
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
    ctx.restore()
  }
}
