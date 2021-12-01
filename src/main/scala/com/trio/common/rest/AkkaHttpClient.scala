package com.trio.common.rest

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.model.headers.OAuth2BearerToken
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{ExecutionContext, Future}

class AkkaHttpClient(implicit actorSystem: ActorSystem) extends RequestBuilding with RestClient with LazyLogging {

  override def request(request: HttpRequest, token: Option[String] = None)(implicit
      ec: ExecutionContext
  ): Future[HttpResponse] = {

    logger.debug(s"REST request: ${request.method.value} ${request.uri}")
    val requestWithToken = token.map(token => request ~> addCredentials(OAuth2BearerToken(token))).getOrElse(request)

    Http()
      .singleRequest(requestWithToken)
      .map { r =>
        logger.debug(s"REST response for ${request.method.value} ${request.uri} ${r.status}")
        r
      }
      .recoverWith {
        case err: Throwable =>
          logger.error(s"REST request failed: ${err.getMessage}")
          Future.failed(err)
      }
  }

}
