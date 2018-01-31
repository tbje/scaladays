package shared

case class MyTweet(user: String, tweet: String)

trait Api {
  def getTweet(): String
}
