package com.trio.api.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.StandardRoute
import com.typesafe.scalalogging.LazyLogging

trait ResponseHandler extends LazyLogging {

  def handleUnexpectedResponse(response: Any): StandardRoute = {
    val message = s"Unexpected response: ${response.toString}"
    logger.error(message)
    complete(StatusCodes.InternalServerError, message)
  }

  def handleFailure(e: Throwable): StandardRoute = {
    val message = "Error when processing request"
    logger.error(message, e)
    complete(StatusCodes.InternalServerError, message)
  }

}
