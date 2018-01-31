package example

import akka.stream.{ Attributes, Outlet, SourceShape }
import akka.stream.stage.{ GraphStage, GraphStageLogic }
import akka.stream.stage.OutHandler
import com.danielasfregola.twitter4s.entities.Tweet

//import scala.concurrent.ExecutionContext.Implicits.global

object TwitterClient {
  private var subscribers = List[Tweet => Unit]()

  def subscribe(f: Tweet => Unit) = subscribers +:= f
  try {
    import com.danielasfregola.twitter4s.TwitterStreamingClient
    val client = TwitterStreamingClient()

    client.filterStatuses(tracks = Seq("#scalazurich")){
    case t: Tweet =>
      subscribers.foreach(_(t))
    }
  } catch {case e: Exception => }
}

class TweetSource extends GraphStage[SourceShape[Tweet]] {
  val out: Outlet[Tweet] = Outlet("TweetSource")

  override val shape: SourceShape[Tweet] = SourceShape(out)

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
    new GraphStageLogic(shape) {

      var buffer: Vector[Tweet] = Vector.empty

      override def preStart(): Unit = {

        def bufferMessageAndEmulatePull(incoming: Tweet): Unit = {
          buffer = buffer :+ incoming
          getHandler(out).onPull()
        }

        val callback = getAsyncCallback[Tweet](bufferMessageAndEmulatePull)

        TwitterClient.subscribe(callback.invoke)

      }

      setHandler(out, new OutHandler {
        override def onPull(): Unit = {
          // we query here because bufferMessageAndEmulatePull artificially calls onPull
          // and we must not violate the GraphStages guarantees
          if (buffer.nonEmpty && isAvailable(out)) {
            val sendValue = buffer.head
            buffer = buffer.drop(1)
            push(out, sendValue)
          }
        }
      })
    }
}
