package example

import scala.scalajs.js

object Txt {

  def txt2(what: String, y: Double, c: Color)(implicit canCtx: CanvasCtx) = {
    val font = "50px Comic Sans MS, cursive, TSCu_Comic, sans-serif"
    canCtx.ctx.save()
    canCtx.ctx.font = font
    c.applyColor(canCtx.ctx)
    val x = canCtx.width / 4
    canCtx.ctx.strokeText(what, x, y, 800)
    canCtx.ctx.fillText(what, x, y, 800)
    canCtx.ctx.restore()
  }

  def txt(start: Double, canCtx: CanvasCtx)(t: Double) = {
    implicit val ctx = canCtx.ctx
    val txt = "sbt new tbje/full-stack.g8"
    val dashLen = 220.0

    val dashOffset = dashLen - ((t - start)  % 220.0)
    val font = "50px Comic Sans MS, cursive, TSCu_Comic, sans-serif"
    ctx.save()
    SvgUtil.withTranslation(200, 200){implicit ctx =>
    ctx.font = font
    ctx.strokeStyle = "red"
    ctx.fillStyle = "red"
    ctx.lineWidth = 5
    ctx.lineJoin = "round"
    ctx.globalAlpha = 0.66
      if (t - start > 220.0*txt.size ) {
        ctx.fillText(txt, 30, 90)
        AppCircuit.dispatch(StopAnimation)
      } else {
        ctx.clearRect(0, 0, 800, 200)
        val letterIndex: Int = math.max(0,(((t-start) / 220.0) % txt.size).toInt)
        val width: Double = txt.take(letterIndex).map(x => ctx.measureText(x.toString).width).sum
        //val spacing = ctx.lineWidth.toDouble * letterIndex
        //ctx.lineWidth
        val x = 30 + width //+ spacing
        val speed = 10

        ctx.setLineDash(js.Array[Double](dashLen - 0, 0 - speed))
        ctx.strokeText(txt.take(letterIndex), 30, 90)
        ctx.fillText(txt.take(letterIndex), 30, 90)
        ctx.setLineDash(js.Array[Double](dashLen - dashOffset, dashOffset - speed))
        ctx.strokeText(txt(letterIndex).toString, x, 90)
      }
    }
      ctx.restore()
  }



}
