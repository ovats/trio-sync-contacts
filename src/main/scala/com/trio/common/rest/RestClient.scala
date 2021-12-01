package com.trio.common.rest

import akka.http.scaladsl.model.{HttpRequest, HttpResponse}

import scala.concurrent.{ExecutionContext, Future}

trait RestClient {

  def request(httpRequest: HttpRequest, token: Option[String] = None)(implicit
      ec: ExecutionContext
  ): Future[HttpResponse]

}
