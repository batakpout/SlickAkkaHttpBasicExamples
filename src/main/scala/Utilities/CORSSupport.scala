package Utilities

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.{Directives, Route}

trait CORSSupport extends Directives {
  val contextRoot: String
  val corsHeaders = List(RawHeader("Access-Control-Allow-Origin", "*"),
    RawHeader("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS, DELETE"),
    RawHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization, Accept-Encoding, Accept-Language, Host, Referer, User-Agent,apiKey"))

  def cors(routes: => Route) = {
    respondWithHeaders(corsHeaders) {
      pathPrefix(contextRoot) {
        routes ~ options {
          complete(StatusCodes.OK)
        }
      }
    }
  }
}