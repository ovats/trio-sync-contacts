package com.trio.api.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.trio.common.mockapi.{MockApiClient, MockApiResponse}
import com.typesafe.scalalogging.LazyLogging
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport

import scala.util.{Failure, Success}

class MockApiRoutes(mockApiClient: MockApiClient) extends LazyLogging with FailFastCirceSupport with ResponseHandler {

  private val serviceName = "mockapi"

  val routes: Route = path(serviceName / "contacts") {
    get {
      logger.debug(s"GET $serviceName/contacts")
      val fut = mockApiClient.getContacts()
      onComplete(fut) {
        case Success(contacts: MockApiResponse.ContactsRetrieved) =>
          complete(StatusCodes.OK, contacts.contacts)
        case Success(response) => handleUnexpectedResponse(response)
        case Failure(e)        => handleFailure(e)
      }
    }
  }
}
