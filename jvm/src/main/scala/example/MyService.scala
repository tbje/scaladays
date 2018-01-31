package example
import shared._

object MyService extends Api {

  def getUser(): Person =
    if (scala.util.Random.nextBoolean())
      Person("Jenny", 27)
    else
      Person("Pierre", 10)

}
