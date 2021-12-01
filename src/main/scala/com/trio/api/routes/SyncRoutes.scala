package com.trio.api.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.trio.common.config.ApiAppConfig
import com.trio.common.mockapi.model.Contact
import com.trio.common.services.{SyncService, SyncServiceResponse}
import com.typesafe.scalalogging.LazyLogging
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.Codec
import io.circe.generic.auto._
import io.circe.generic.semiauto.deriveCodec

import scala.util.{Failure, Success}

class SyncRoutes(syncService: SyncService, config: ApiAppConfig)
    extends LazyLogging
    with ResponseHandler
    with FailFastCirceSupport {

  private val serviceName = config.api.serviceName

  val routes: Route = path(serviceName / "sync") {
    get {
      logger.debug(s"GET /$serviceName/sync")
      val fut = syncService.syncContacts()
      onComplete(fut) {
        case Success(SyncServiceResponse.SyncPerformed(contacts)) =>
          val result =
            SyncResult(
              syncedContacts = contacts.size,
              contacts = contacts,
            )
          complete(StatusCodes.OK, result)
        case Success(response) => handleUnexpectedResponse(response)
        case Failure(e)        => handleFailure(e)
      }
    }
  }

}

final case class SyncResult(syncedContacts: Int, contacts: Seq[Contact])
object SyncResult {
  implicit val codec: Codec[SyncResult] = deriveCodec[SyncResult]
}
