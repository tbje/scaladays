package example

import scala.scalajs.js

object Txt {

  def writeText(msg: String, font: String, xPos: Double, yPos: Double, maxSize: Double, color: Option[Color] = None)(implicit cctx: CanvasCtx) = {
    val ctx = cctx.ctx
    ctx.save()
    ctx.font = font
    color.map(c => c.applyColor(ctx))
    ctx.strokeText(msg, xPos, yPos, maxSize)
    ctx.fillText(msg, xPos, yPos, maxSize)
    ctx.restore()
  }

  def writeTitle(str: String, color: Option[Color] = None)(implicit ctx: CanvasCtx) =
    writeText(str, "70px Comic Sans MS", 90, 100, 600, color)

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

  def txt3(what: String, x: Double, y: Double, c: Color)(implicit canCtx: CanvasCtx) = {
    val font = "50px Comic Sans MS, cursive, TSCu_Comic, sans-serif"
    canCtx.ctx.save()
    canCtx.ctx.font = font
    c.applyColor(canCtx.ctx)
    canCtx.ctx.strokeText(what, x, y, 800)
    canCtx.ctx.fillText(what, x, y, 800)
    canCtx.ctx.restore()
  }
  def txt4(what: String, x: Double, y: Double, c: Color)(implicit canCtx: CanvasCtx) = {
    val font = "30px monospace, normal, normal"
    canCtx.ctx.save()
    canCtx.ctx.font = font
    c.applyColor(canCtx.ctx)
    canCtx.ctx.strokeText(what, x, y, 800)
    canCtx.ctx.fillText(what, x, y, 800)
    canCtx.ctx.restore()
  }


  def txt(start2: Double, canCtx: CanvasCtx)(t: Double) = {
    implicit val c = canCtx
    val ctx = canCtx.ctx
    val start = start2
    val txt = "sbt new tbje/full-stack.g8"

    val font = "50px Comic Sans MS, cursive, TSCu_Comic, sans-serif"
    import Presentation.{sdBlue, white }
    ctx.save()
    ctx.fillStyle = sdBlue.fill.get
    ctx.fillRect(0, 0, c.width, c.height)
    ctx.fillStyle = "black"
    ctx.fillRect(170, 150, c.width-250, c.height-250)
    Txt.writeTitle("Getting started", Some(white))
    Txt.txt3("~ >", 200, 230, Color("green"))
    SvgUtil.withTranslation(246, 138){ implicit c =>
      val ctx = c.ctx
      ctx.font = font
      ctx.strokeStyle = "white"
      ctx.fillStyle = "white"
      ctx.lineWidth = 5
      ctx.lineJoin = "round"
      val times = Seq(200, 80, 10, 10, 30, 60, 10, 5, 9, 10, 2, 33, 10, 34, 10, 50, 90, 10, 20, 33, 50, 34, 20, 33, 50, 34, 50, 34).map(_ * 7)
      val totals = times.foldLeft(0 -> Seq[Int]()){ case ((sum, s), el) => (sum + el) -> (s :+ sum) }._2
      if (t - start > times.sum ) {
        ctx.fillText(txt, 30, 90)
        AppCircuit.dispatch(StopAnimation)
      } else {
        val letterIndex2: Int = totals.zipWithIndex.find(_._1 > (t-start)).map(_._2 - 1).getOrElse(txt.size)
        ctx.fillText(txt.take(letterIndex2), 30, 90)
      }
    }
    ctx.restore()
  }



}
