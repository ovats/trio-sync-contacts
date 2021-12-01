package com.trio.common.rest

import akka.http.scaladsl.model.Uri.Path
import akka.http.scaladsl.model._

import scala.concurrent.{ExecutionContext, Future}

class RestClientMock extends RestClient {

  private val responseMap = new scala.collection.mutable.HashMap[(HttpMethod, Path), HttpResponse]

  override def request(httpRequest: HttpRequest, token: Option[String])(implicit
      ec: ExecutionContext
  ): Future[HttpResponse] = {
    val responseOpt = responseMap.get((httpRequest.method, httpRequest.uri.path))
    responseOpt match {
      case Some(response) => Future.successful(response)
      case None =>
        throw new UnsupportedOperationException(
          s"Request ${httpRequest.method} ${httpRequest.uri.path} does not have" +
              s" a response configured in RestClientMock. Defined responses: $responseMap"
        )
    }
  }

  def clearResponses(): Unit = responseMap.clear()

  def addResponse[T <: AnyRef](
      requestMethod: HttpMethod,
      requestPath: String,
      responseStatus: StatusCode = StatusCodes.OK,
      responseContent: String,
  ) = {
    val response =
      HttpResponse(status = responseStatus, entity = HttpEntity(ContentTypes.`application/json`, responseContent))
    responseMap += (requestMethod, Path(requestPath)) -> response
  }

}
