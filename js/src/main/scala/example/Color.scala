package example

import org.scalajs.dom.ext.{ Color => ExtColor }
import org.scalajs.dom.raw.{ CanvasRenderingContext2D => Ctx }

object Color {
  import scala.language.implicitConversions
  implicit def extColorToColor(extColor: ExtColor) =
    Color.apply(extColor.toHex)
  def apply(fill: String) = fill match {
    case "rand" => rand()
    case fill =>
      new Color(Some(fill), None)
  }
  def rand() = {
    val colors = Seq("#ea4335", "#7cbf00", "#00a1f1", "yellow", "orange", /*"pink", */"cyan", "magenta")
    new Color(colors.lift(util.Random.nextInt(colors.size)), None)
  }
}

case class Color(fill: Option[String], stroke: Option[(Int, String)]) {
  def applyColor(ctx: Ctx): Unit = {
    val s: String = fill.getOrElse("black")
    ctx.fillStyle = s//(fill.getOrElse("black"))
    stroke match {
      case Some((size, color)) =>
        ctx.strokeStyle = color
        ctx.lineWidth = size.toDouble
      case None =>
        ctx.strokeStyle = s
        ctx.lineWidth = 1
    }
  }
}
