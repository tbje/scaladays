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

  def apply()(implicit s: ActorSystem, m : Materializer, e: ExecutionContext): Route = {

    pathSingleSlash {
      get {
        complete {
          HttpEntity(ContentTypes.`text/html(UTF-8)`, string =
            s"""|<!DOCTYPE html>
                |<html>
                |  <head>
                |    <title>Hello Full Stack Scala</title>
                |  </head>
                |  <body>
                |   <div id="${Ids.main}">Trying to get time from server ...</div>
                |   <img src="static/airplane.svg"/>
                |   <script src="/assets/client-fastopt.js" type="text/javascript"></script>
                |  </body>
                |</html>""".stripMargin)
        }
      }
    } ~
    ((pathPrefix("assets" / Remaining) & respondWithHeader(`Cache-Control`(`no-cache`)))) { file =>
      // optionally compresses the response with Gzip or Deflate
      // if the client accepts compressed responses
      getFromResource("public/" + file)
    } ~
    path("api" / Segments) { segments =>
      post(AutowireServer.dispatch(segments))
    } ~
    path("static" / Remaining) { file =>
      get(
        getFromFile(s"/home/tbje/full-stack2/full-stack/jvm/src/main/resources/$file")
      )
     } ~
    path("events") {
      get {
        complete {
          Source.fromGraph(new TweetSource)
            .map(tweet => SSE(tweet.user.map(_.screen_name).getOrElse("") + ": " + tweet.text))
            .throttle(1, 1.second, 4, Shaping)
            .keepAlive(1.second, () => SSE.heartbeat)
        }
      }
    }
  }
}
