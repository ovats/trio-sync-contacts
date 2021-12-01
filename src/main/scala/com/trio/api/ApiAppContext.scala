package com.trio.api

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Route
import com.trio.common.config.ApiAppConfig
import com.trio.common.mailchimp.MailChimpClient
import com.trio.common.rest.{AkkaHttpClient, RestClient}
import com.trio.api.routes.{MailChimpRoutes, MockApiRoutes, PingRoutes, SyncRoutes}
import akka.http.scaladsl.server.Directives._
import com.trio.common.mockapi.MockApiClient
import com.trio.common.services.SyncService

import scala.concurrent.ExecutionContext

class ApiAppContext(implicit actorSystem: ActorSystem, ec: ExecutionContext) {

  // Configuration values in application.conf
  val config: ApiAppConfig = ApiAppConfig()

  // Rest Clients
  val restClient: RestClient           = new AkkaHttpClient()
  val mailChimpClient: MailChimpClient = new MailChimpClient(restClient, config)
  val mockApiClient: MockApiClient     = new MockApiClient(restClient, config)

  // Services
  val syncService: SyncService = new SyncService(mailChimpClient, mockApiClient, config)

  // Routes
  val pingRoutes: PingRoutes           = new PingRoutes(config)
  val mailChimpRoutes: MailChimpRoutes = new MailChimpRoutes(mailChimpClient, config)
  val mockApiRoutes: MockApiRoutes     = new MockApiRoutes(mockApiClient)
  val syncRoutes: SyncRoutes           = new SyncRoutes(syncService, config)
  val routes: Route                    = pingRoutes.routes ~ mailChimpRoutes.routes ~ mockApiRoutes.routes ~ syncRoutes.routes
}
