package example

import org.scalajs.dom.raw.{ CanvasRenderingContext2D => Ctx, HTMLImageElement }
import SvgUtil._
import scala.concurrent.{ Future, Promise }

object Images {
  def drawIsland()(implicit ctx: Ctx) = {
    withPath(Color(Some("green"), Some(1 -> "black"))){ctx =>
      ctx.moveTo(140.76834,567.84201)
      ctx.lineTo(220.80169,561.72626)
      ctx.bezierCurveTo(160.85445, 532.93072, 232.90722, 380.13518, 240.95998, 358.33964)
      ctx.bezierCurveTo(269.39235, 369.84439, 253.82472, 416.34913, 309.25709, 434.85388)
      ctx.bezierCurveTo(286.35613, 394.69196, 293.45516, 351.53003, 243.55420, 350.36811)
      ctx.bezierCurveTo(293.99563, 345.49430, 323.43705, 315.62048, 333.87848, 300.74667)
      ctx.bezierCurveTo(241.98657, 320.87286, 262.09467, 327.99904, 238.20276, 342.12523)
      ctx.bezierCurveTo(243.08561, 322.04427, 284.96847, 301.96331, 270.85132, 256.88235)
      ctx.bezierCurveTo(254.95941, 284.84187, 224.06751, 297.80138, 223.17560, 340.76090)
      ctx.bezierCurveTo(194.39631, 287.90733, 165.61702, 313.05375, 126.83773, 300.20018)
      ctx.bezierCurveTo(120.05844, 312.34661, 162.27916, 312.49303, 216.49987, 348.63946)
      ctx.bezierCurveTo(159.27463, 346.59898, 139.04939, 401.55850, 140.82415, 414.51802)
      ctx.bezierCurveTo(171.93224, 373.81087, 178.04034, 395.10373, 228.14843, 354.39658)
      ctx.bezierCurveTo(118.27846, 569.45788, 149.83933, 544.37875, 140.76834, 567.84201)
    }
    withPath(Color("yellow")){ctx =>
      ctx.moveTo(297.15179,591.06696)
      ctx.bezierCurveTo(177.67220, 592.60884, 75.531250, 587.59375, 75.531250, 587.59375)
      ctx.lineTo(74.665179,588.56250)
      ctx.bezierCurveTo(74.665179, 588.56250, 276.20043, 474.41657, 488.21875, 588.56250)
      ctx.lineTo(490.17857,589.02232)
      ctx.bezierCurveTo(425.69019, 595.40284, 359.73634, 590.25931, 297.15179, 591.06696)
    }
  }

  def drawMan()(implicit ctx: Ctx) = {
    withPath(Color("black")){ ctx =>
      ctx.moveTo(212.828, 128.61)
      ctx.lineTo(127.188, 217.642)
      ctx.lineTo(144.994, 235.448)
      ctx.lineTo(212.828, 163.375)

      ctx.lineTo(213.677, 284.627)
      ctx.lineTo(155.17, 465.235)
      ctx.bezierCurveTo(155.17, 465.235, 193.327, 466.186, 194.175, 464.49)
      ctx.bezierCurveTo(194.598, 463.642, 236.57, 297.347, 238.266, 294.803)
      ctx.lineTo(285.75, 464.387)
      ctx.lineTo(322.21, 464.387)
      ctx.lineTo(263.704, 285.475)
      ctx.lineTo(264.552, 163.375)
      ctx.lineTo(334.081, 233.753)
      ctx.lineTo(354.432, 216.794)
      ctx.lineTo(266.248, 128.61)
      ctx.lineTo(212.828, 128.61)
    }
    withPath(Color("black")){ctx =>
      ctx.moveTo(239.114, 12.4448)
      ctx.bezierCurveTo(159.409, 14.1406, 160.257, 118.435, 236.57, 119.283)
      ctx.bezierCurveTo(318.819, 119.283, 321.362, 13.2928, 239.114, 12.4448)
      ctx.closePath()
    }
  }

  import org.scalajs.dom

  def loadImage(name: String): Future[HTMLImageElement] = {
    val image = dom.document.createElement("img").asInstanceOf[HTMLImageElement]
    image.src = s"static/$name"
    val promise: Promise[HTMLImageElement] = Promise[HTMLImageElement]()
    image.onload = (e: dom.Event) => promise.success(image)
    promise.future
  }

  def drawImage2(x: HTMLImageElement, posX: Double, posY: Double, scale: Double)(implicit ctx: Ctx) =
    ctx.drawImage(x, 0, 0, x.width, x.height, posX, posY, x.width * scale, x.height * scale)

  import scala.concurrent.ExecutionContext.Implicits.global

  def writeText(msg: String, font: String, xPos: Double, yPos: Double, maxSize: Double)(implicit ctx: Ctx) = {
    val f = ctx.font
    ctx.font = font
    ctx.strokeText(msg, xPos, yPos, maxSize)
    ctx.fillText(msg, xPos, yPos, maxSize)
    ctx.font = f
  }

  def backInTheDays()(implicit ctx: Ctx) = {
    Future.sequence(List(loadImage("stone-age.gif"), loadImage("java.png"), loadImage("scala.png"))).onSuccess{ case List(stone, java, scala)  =>
      writeText("Back in the days...", "70px Comic Sans MS", 90, 100, 600)
      drawImage2(scala, 50, 170, 0.8)
      drawImage2(java, 430, 170, 0.5)
      drawImage2(stone, 780, 50, 1)
      drawArrow()
    }
  }

  def today()(implicit ctx: Ctx) = {
    Future.sequence(List(loadImage("today.png"), loadImage("java.png"), loadImage("scala.png"), loadImage("js-logo.jpg"), loadImage("llvm.png"))).onSuccess{
      case List(today, java, scala, js, llvm)  =>
        writeText("Today...", "70px Comic Sans MS", 90, 100, 600)
        drawImage2(scala, 50, 390, 0.8)
        drawImage2(java, 430, 170, 0.5)
        drawImage2(js, 430, 400, 0.6)
        //drawImage2(today, 50, 50, 0.6)
        drawImage2(llvm, 450, 650, 0.9)
        withTranslation(350, 450+50){implicit ctx: Ctx =>
          drawArrow2()
        }
        withTranslation(350, 600+50){implicit ctx: Ctx =>
          ctx.rotate(30*Math.PI/180)
          drawArrow2()
          ctx.rotate(-30*Math.PI/180)
        }
        withTranslation(350, 300+50){implicit ctx: Ctx =>
          ctx.rotate(-30*Math.PI/180)
          drawArrow2()
          ctx.rotate(+30*Math.PI/180)
        }
    }
  }

  def drawSea(canvasWidth: Double, canvasHeight: Double, height: Double)(implicit ctx: Ctx) =
    withPath(Color("blue")){ ctx =>
      ctx.moveTo(0, height)
      ctx.lineTo(canvasWidth, height)
      ctx.lineTo(canvasWidth, canvasHeight)
      ctx.lineTo(0, canvasHeight)
    }

  def drawBlueSky(canvasWidth: Double, canvasHeight: Double, height: Double)(implicit ctx: Ctx) =
    withPath(Color("#87CEFA")){ ctx =>
      ctx.moveTo(0,0)
      ctx.lineTo(0, height)
      ctx.lineTo(canvasWidth, height)
      ctx.lineTo(canvasWidth, 0)
      ctx.lineTo(0, 0)
    }

  def drawBouble()(implicit ctx: Ctx) =
    withPath(Color(Some("white"), Some(2 -> "black"))){ ctx =>
      ctx.moveTo(150, 290)
      ctx.lineTo(175, 263)
      ctx.bezierCurveTo(12, 159, 420, 166, 204, 265)
    }

  def drawSun()(implicit ctx: Ctx) =
    withPath(Color("yellow")){ctx =>
      ctx.arc(130, 130, 100, 0, 2*Math.PI)
    }


  def drawArrow()(implicit ctx: Ctx) = {
    withPath(Color("pink")){ ctx=>
      val start = (250, 250)
      ctx.moveTo(start._1, start._2 - 15)
      ctx.lineTo(start._1 + 100, start._2 - 15)
      ctx.lineTo(start._1 + 100, start._2 - 40)
      ctx.lineTo(start._1 + 140, start._2)
      ctx.lineTo(start._1 + 100, start._2 + 40)
      ctx.lineTo(start._1 + 100, start._2 + 15)
      ctx.lineTo(start._1, start._2 + 15)
   }
  }

  def drawArrow2()(implicit ctx: Ctx) = {
    withPath(Color("pink")){ ctx=>
      val start = (-70, 0)
      ctx.moveTo(start._1, start._2 - 15)
      ctx.lineTo(start._1 + 100, start._2 - 15)
      ctx.lineTo(start._1 + 100, start._2 - 40)
      ctx.lineTo(start._1 + 140, start._2)
      ctx.lineTo(start._1 + 100, start._2 + 40)
      ctx.lineTo(start._1 + 100, start._2 + 15)
      ctx.lineTo(start._1, start._2 + 15)
   }
  }

  def drawImage(canvasWidth: Double, canvasHeight: Double)(implicit ctx: Ctx) = {
    val seaHight: Double = 500
    drawSea(canvasWidth, canvasHeight, seaHight)
    drawBlueSky(canvasWidth, canvasHeight, seaHight)

    withTranslation(-70, 0){implicit ctx => drawIsland() }

    withTranslation(540, 0){implicit ctx => drawIsland() }

    withTranslation(130, 480){implicit ctx =>
      withScale(0.2, 0.2){implicit ctx =>
        drawMan()
      }
    }

    withTranslation(700, 480){implicit ctx =>
      withScale(0.2, 0.2){implicit ctx =>
        drawMan()
      }
    }

    drawSun()

    withTranslation(50, 200){implicit ctx =>
      drawBouble()
    }

    withTranslation(860, 200){implicit ctx =>
      withScale(-1, 1){implicit ctx =>
        drawBouble()
      }
    }

    withScale(2, 2){ ctx =>
      ctx.strokeText("Bonjour", 105, 209, 300)
    }

    withTranslation(610, 420){implicit ctx =>
      withScale(1.6, 1.6){implicit ctx =>
        ctx.strokeText("Hei på deg!", 0, 0, 300)
      }
    }
  }


}
