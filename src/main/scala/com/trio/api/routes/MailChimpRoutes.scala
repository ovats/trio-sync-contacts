package com.trio.api.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.trio.common.config.ApiAppConfig
import com.trio.common.mailchimp.{MailChimpClient, MailChimpResponse}
import com.typesafe.scalalogging.LazyLogging
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport

import scala.util.{Failure, Success}

class MailChimpRoutes(mailChimpClient: MailChimpClient, config: ApiAppConfig)
    extends LazyLogging
    with FailFastCirceSupport
    with ResponseHandler {

  private val serviceName = "mailchimp"

  private val ping = path(serviceName / "ping") {
    get {
      logger.debug(s"GET $serviceName/ping")
      val fut = mailChimpClient.ping()
      onComplete(fut) {
        case Success(MailChimpResponse.PingSuccessfully) =>
          complete(StatusCodes.OK, "Ping successfully")
        case Success(MailChimpResponse.PingError) =>
          complete(StatusCodes.OK, "Ping error")
        case Success(response) => handleUnexpectedResponse(response)
        case Failure(e)        => handleFailure(e)
      }
    }
  }

  private val getListsInfo = path(serviceName / "lists") {
    get {
      logger.debug(s"GET $serviceName/lists")
      val fut = mailChimpClient.getListsInfo()
      onComplete(fut) {
        case Success(list: MailChimpResponse.AudienceListRetrieved) =>
          complete(StatusCodes.OK, list.audiences)
        case Success(response) => handleUnexpectedResponse(response)
        case Failure(e)        => handleFailure(e)
      }
    }
  }

  private val getPredefinedListInfo = path(serviceName / "predefinedlist") {
    get {
      logger.debug(s"GET $serviceName/predefinedlist")
      val fut = mailChimpClient.getListInfo(config.mailchimp.listIdToSync)
      onComplete(fut) {
        case Success(list: MailChimpResponse.AudienceRetrieved) =>
          complete(StatusCodes.OK, list.audience)
        case Success(response) => handleUnexpectedResponse(response)
        case Failure(e)        => handleFailure(e)
      }
    }
  }

  private val getMembers = path(serviceName / "members") {
    get {
      logger.debug(s"GET $serviceName/members")
      val fut = mailChimpClient.getMembersOfList(config.mailchimp.listIdToSync)
      onComplete(fut) {
        case Success(list: MailChimpResponse.MembersListRetrieved) =>
          complete(StatusCodes.OK, list.members)
        case Success(response) => handleUnexpectedResponse(response)
        case Failure(e)        => handleFailure(e)
      }
    }
  }

  val routes: Route = ping ~ getListsInfo ~ getPredefinedListInfo ~ getMembers

}
