package com.trio.api.routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.trio.common.config.ApiAppConfig
import com.typesafe.scalalogging.LazyLogging

class PingRoutes(config: ApiAppConfig) extends LazyLogging {

  private val serviceName = config.api.serviceName

  val routes: Route = path(serviceName / "ping") {
    get {
      logger.debug(s"GET /$serviceName/ping")
      complete("pong")
    }
  }
}
