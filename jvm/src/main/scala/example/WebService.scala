package example
import shared.Ids
import akka.http.scaladsl.model.{ ContentType, ContentTypes, HttpCharset, HttpEntity }
import akka.http.scaladsl.model.StatusCodes.Success
import akka.http.scaladsl.model.{ HttpHeader, HttpResponse }
import akka.http.scaladsl.model.headers.{ `Cache-Control` }
import akka.http.scaladsl.model.headers.`Content-Type`
import akka.http.scaladsl.model.headers.CacheDirectives._


import akka.http.scaladsl.server.Directives
import scala.concurrent.ExecutionContext
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import akka.actor.ActorSystem

import akka.http.scaladsl.marshalling.sse.EventStreamMarshalling._
import akka.NotUsed
import akka.stream.scaladsl.Source

import akka.http.scaladsl.Http
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.model.sse.ServerSentEvent
import scala.concurrent.duration._

import java.time.LocalTime
import java.time.format.DateTimeFormatter.ISO_LOCAL_TIME

class WebService() extends Directives {
  def apply()(implicit s: ActorSystem, m: Materializer, e: ExecutionContext): Route = {

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
    path("events") {
      get {
        complete {
          Source
            .tick(2.seconds, 2.seconds, NotUsed)
            .map(_ => LocalTime.now())
            .map(time => ServerSentEvent(ISO_LOCAL_TIME.format(time)))
            .keepAlive(1.second, () => ServerSentEvent.heartbeat)
        }
      }
    }
  }
}
