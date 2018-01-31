package example
import com.danielasfregola.twitter4s.entities.Tweet
import scala.concurrent.{ Await, Promise }
import scala.util.Try
import shared._

object MyService extends Api {

  import com.danielasfregola.twitter4s.TwitterStreamingClient
  val client = TwitterStreamingClient()
  import scala.concurrent.duration._

  def getTweet() = "Hello"
//  def getTweet(hashTag: String): Either[String, MyTweet] = {
//    val p = Promise[MyTweet]()
//    client.filterStatuses(tracks = Seq(hashTag)) {
//      case t: Tweet =>
//        println(t.text)
//        p.success(MyTweet(t.user.map(_.screen_name).getOrElse(""), t.text))
//    }
//
//    Try( Await.result(p.future, 10.seconds)).toEither.left.map(_ => "no tweets yet failed")
//  }


}
