package com.trio.common.mailchimp

import akka.actor.ActorSystem
import com.trio.common.rest.RestClient
import akka.http.scaladsl.client.RequestBuilding.{Delete, Get, Patch, Post, Put}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.trio.common.MailService
import com.trio.common.config.ApiAppConfig
import com.trio.common.mailchimp.model.{Audience, AudienceList, MemberDataRequest, MemberList}
import com.typesafe.scalalogging.LazyLogging
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport

import scala.concurrent.{ExecutionContext, Future}

class MailChimpClient(restClient: RestClient, config: ApiAppConfig)(implicit
    actorSystem: ActorSystem,
    ec: ExecutionContext,
) extends MailService
    with LazyLogging
    with FailFastCirceSupport {

  override def ping(): Future[MailChimpResponse] = {
    restClient
      .request(Get(MailChimpUrl.pingUrl(config.mailchimp.url)), Option(config.mailchimp.apiKey))
      .flatMap { response =>
        response.status match {
          case StatusCodes.OK => Future.successful(MailChimpResponse.PingSuccessfully)
          case _              => Future.successful(MailChimpResponse.PingError)
        }
      }
  }

  override def getListsInfo(): Future[MailChimpResponse] = {
    restClient
      .request(
        Get(
          MailChimpUrl.getListsInfoUrl(baseUrl = config.mailchimp.url, count = config.mailchimp.maxResultsPagination)
        ),
        Option(config.mailchimp.apiKey),
      )
      .flatMap { response =>
        response.status match {
          case StatusCodes.OK =>
            Unmarshal(response)
              .to[AudienceList]
              .map(MailChimpResponse.AudienceListRetrieved)
              .recoverWith(handleException("Error when processing response in getListsInfo"))
          case _ =>
            handleUnknownResponse(s"Error in getListsInf, response ${response.status}")
        }
      }
  }

  override def getListInfo(listId: String): Future[MailChimpResponse] = {
    restClient
      .request(Get(MailChimpUrl.getListInfoUrl(config.mailchimp.url, listId)), Option(config.mailchimp.apiKey))
      .flatMap { response =>
        response.status match {
          case StatusCodes.OK =>
            Unmarshal(response)
              .to[Audience]
              .map(MailChimpResponse.AudienceRetrieved)
              .recoverWith(handleException("Error when processing response in getListInfo"))
          case _ =>
            handleUnknownResponse(s"Error in getListInfo $listId, response ${response.status}")
        }
      }
  }

  override def getMembersOfList(listId: String): Future[MailChimpResponse] = {
    restClient
      .request(
        Get(
          MailChimpUrl
            .getMembersOfListUrl(
              baseUrl = config.mailchimp.url,
              listId = listId,
              count = config.mailchimp.maxResultsPagination,
            )
        ),
        Option(config.mailchimp.apiKey),
      )
      .flatMap { response =>
        response.status match {
          case StatusCodes.OK =>
            Unmarshal(response)
              .to[MemberList]
              .map(MailChimpResponse.MembersListRetrieved)
              .recoverWith(handleException("Error when processing response in getMembersOfList"))
          case _ =>
            val errorMessage = s"Error in getMembersOfList listId $listId, response ${response.status}"
            handleUnknownResponse(errorMessage)
        }
      }
  }

  override def addMemberToList(listId: String, member: MemberDataRequest): Future[MailChimpResponse] = {
    restClient
      .request(Post(MailChimpUrl.addMemberUrl(config.mailchimp.url, listId), member), Option(config.mailchimp.apiKey))
      .flatMap { response =>
        response.status match {
          case StatusCodes.OK =>
            Future.successful(MailChimpResponse.MemberAdded(member.email_address))
          case _ =>
            val errorMessage = s"Error in addMemberToList listId $listId, response ${response.status}"
            handleUnknownResponse(errorMessage)
        }
      }
  }

  override def updateMemberInList(listId: String, member: MemberDataRequest): Future[MailChimpResponse] = {
    restClient
      .request(
        Patch(MailChimpUrl.updateMemberUrl(config.mailchimp.url, listId, member.email_address), member),
        Option(config.mailchimp.apiKey),
      )
      .flatMap { response =>
        response.status match {
          case StatusCodes.OK =>
            Future.successful(MailChimpResponse.MemberUpdated(member.email_address))
          case _ =>
            val errorMessage =
              s"Error in updateMember listId $listId, email ${member.email_address}, response ${response.status}"
            handleUnknownResponse(errorMessage)
        }
      }
  }

  override def permanentDeleteMemberInList(listId: String, email: String): Future[MailChimpResponse] = {
    restClient
      .request(
        Post(MailChimpUrl.permanentDeleteMemberUrl(config.mailchimp.url, listId, email)),
        Option(config.mailchimp.apiKey),
      )
      .flatMap { response =>
        response.status match {
          case StatusCodes.NoContent =>
            Future.successful(MailChimpResponse.MemberPermanentDeleted(email))
          case _ =>
            val errorMessage = s"Error when deleting member $email, response ${response.status}"
            handleUnknownResponse(errorMessage)
        }
      }
  }

  private def handleUnknownResponse(errorMessage: String) = {
    logger.error(errorMessage)
    Future.successful(MailChimpResponse.Error(errorMessage))
  }

  private def handleException(errorMessage: String): PartialFunction[Throwable, Future[MailChimpResponse]] = {
    case e: Throwable =>
      logger.error(errorMessage, e)
      Future.successful(MailChimpResponse.Error(errorMessage))
  }

}
