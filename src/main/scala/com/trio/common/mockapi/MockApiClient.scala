package com.trio.common.mockapi

import akka.actor.ActorSystem
import akka.http.scaladsl.client.RequestBuilding.Get
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.trio.common.ContactsService
import com.trio.common.config.ApiAppConfig
import com.trio.common.mockapi.model.Contact
import com.trio.common.rest.RestClient
import com.typesafe.scalalogging.LazyLogging
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport

import scala.concurrent.{ExecutionContext, Future}

class MockApiClient(restClient: RestClient, config: ApiAppConfig)(implicit
    actorSystem: ActorSystem,
    ec: ExecutionContext,
) extends ContactsService
    with LazyLogging
    with FailFastCirceSupport {

  override def getContacts(): Future[MockApiResponse] = {
    restClient
      .request(Get(MockApiUrl.getContactsUrl(config.mockapi.url)))
      .flatMap { response =>
        response.status match {
          case StatusCodes.OK =>
            Unmarshal(response)
              .to[List[Contact]]
              .map(MockApiResponse.ContactsRetrieved)
              .recoverWith(handleException("Error when processing response in getContacts"))
          case _ =>
            Future.successful(MockApiResponse.Error("Error in getContacts"))
        }
      }

  }

  private def handleException(errorMessage: String): PartialFunction[Throwable, Future[MockApiResponse]] = {
    case e: Throwable =>
      logger.error(errorMessage, e)
      Future.successful(MockApiResponse.Error(errorMessage))
  }

}
