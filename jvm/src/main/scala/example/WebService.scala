package example
import akka.stream.ThrottleMode.Shaping
import shared.Ids
import akka.http.scaladsl.model.{ ContentTypes, HttpEntity }
import akka.http.scaladsl.model.headers.`Cache-Control`
import akka.http.scaladsl.model.headers.CacheDirectives._


import akka.http.scaladsl.server.Directives
import scala.concurrent.ExecutionContext
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import akka.actor.ActorSystem

import akka.http.scaladsl.marshalling.sse.EventStreamMarshalling._
import akka.stream.scaladsl.Source

import akka.http.scaladsl.model.sse.{ ServerSentEvent => SSE }
import scala.concurrent.duration._

class WebService() extends Directives {

  val mainHtml = s"""|<!DOCTYPE html>
                     |<html>
                     |  <head>
                     |    <title>Full Stack Scala</title>
                     |    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.1/css/bootstrap.min.css" integrity="sha384-WskhaSGFgHYWDcbwN70/dfYBj47jz9qbsMId/iRN3ewGhXQFZCSftd1LZCfmhktB" crossorigin="anonymous">
                     |  </head>
                     |  <body>
                     |   <div id="${Ids.main}"></div>
                     |   <script src="/js/client-fastopt.js" type="text/javascript"></script>
                     |  </body>
                     |</html>""".stripMargin

  val mainReply = HttpEntity(ContentTypes.`text/html(UTF-8)`, string = mainHtml)

  def apply()(implicit s: ActorSystem, m : Materializer, e: ExecutionContext): Route = {

    (pathSingleSlash | (pathPrefix("dev" | "pres"))) {
      get(complete(mainReply))
    } ~
    ((pathPrefix("assets" / Remaining) & respondWithHeader(`Cache-Control`(`no-cache`)))) { file =>
      // optionally compresses the response with Gzip or Deflate
      // if the client accepts compressed responses
      getFromResource("public" + file)
    } ~
    ((pathPrefix("js" / Remaining) & respondWithHeader(`Cache-Control`(`no-cache`)))) { file =>
      // optionally compresses the response with Gzip or Deflate
      // if the client accepts compressed responses
      getFromFile("/home/tbje/scaladays/js/target/scala-2.12/" + file)
    } ~
    path("api" / Segments) { segments =>
      post(AutowireServer.dispatch(segments))
    } ~
    path("static" / Remaining) { file =>
      get(
        getFromFile(s"/home/tbje/scaladays/jvm/src/main/resources/$file")
      )
     } ~
    path("events") {
      get {
        complete {
          import boopickle.Default._
          Source.fromGraph(new TweetSource)
            .map(tweet => SSE(tweet.user.map(_.screen_name).getOrElse("") + " : " + tweet.text))
            .throttle(1, 1.second, 4, Shaping)
            .keepAlive(1.second, () => SSE.heartbeat)
        }
      }
    }
  }
}
