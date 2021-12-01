package com.trio.api

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import com.typesafe.scalalogging.LazyLogging

import scala.util.{Failure, Properties, Success}

object MainApiApp extends LazyLogging {

  def main(args: Array[String]): Unit = {
    logger.info("Starting MainApiApp ...")

    implicit val system: ActorSystem = ActorSystem("MainApiApp")
    import system.dispatcher

    // Context data of the app
    val appContext: ApiAppContext = new ApiAppContext()

    // Server
    val host: String = appContext.config.api.host
    val port: Int    = Properties.envOrElse("PORT", appContext.config.api.port).toInt

    Http()
      .newServerAt(host, port)
      .bind(appContext.routes)
      .onComplete {
        case Success(_) => logger.info(s"Started at port $port")
        case Failure(e) => logger.error("Failed to start", e)
      }
  }

}
