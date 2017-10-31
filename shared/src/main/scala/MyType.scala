package shared

object MyType {
  import boopickle.Default._
  implicit val datePickler = transformPickler((t: Long) => new java.util.Date(t))(_.getTime)
}

case class MyType(msg: String, time: java.util.Date)

object TweetParser {

  sealed trait Forme
  case class Circle(di: Int) extends Forme
  case class Carre(height: Int) extends Forme
  case class Triangle(height: Int) extends Forme

  val circle = ".*circle(34).*".r

  def parse(tweet: String) = tweet match {
    case circle(diametre) => diametre
  }


}
