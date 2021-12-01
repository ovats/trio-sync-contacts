package com.trio.routes

import akka.http.scaladsl.model.{HttpMethods, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.trio.api.routes.{SyncResult, SyncRoutes}
import com.trio.common.config.ApiAppConfig
import com.trio.common.mailchimp.MailChimpClient
import com.trio.common.mailchimp.model.MemberList
import com.trio.common.mockapi.MockApiClient
import com.trio.common.mockapi.model.Contact
import com.trio.common.rest.RestClientMock
import com.trio.common.services.SyncService
import com.trio.routes.TestData.{contact1, contact2, contact3, member1, member2}
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.syntax.EncoderOps
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers

class SyncRoutesSpec
    extends AnyFlatSpecLike
    with Matchers
    with ScalatestRouteTest
    with FailFastCirceSupport
    with BeforeAndAfterEach {

  private val config: ApiAppConfig = ApiAppConfig()
  private val restClient           = new RestClientMock()
  private val mailChimpClient      = new MailChimpClient(restClient, config)
  private val mockApiClient        = new MockApiClient(restClient, config)
  private val syncService          = new SyncService(mailChimpClient, mockApiClient, config)
  private val syncRoutes           = new SyncRoutes(syncService, config).routes

  private def setContactsMailChimp(responseString: String = "") = {
    restClient.addResponse(
      HttpMethods.GET,
      s"/3.0/lists/${config.mailchimp.listIdToSync}/members",
      StatusCodes.OK,
      responseString,
    )
  }

  private def setContactsMockApi(responseAsString: String = "") = {
    restClient.addResponse(
      HttpMethods.GET,
      s"/contacts",
      StatusCodes.OK,
      responseAsString,
    )
  }

  s"GET /${config.api.serviceName}/sync" should "return no sync when there's no data to sync" in {
    setContactsMockApi(List.empty[Contact].asJson.toString())
    setContactsMailChimp(MemberList(List.empty, "", 0).asJson.toString())

    val request = Get(uri = s"/${config.api.serviceName}/sync")
    request ~> syncRoutes ~> check {
      status shouldBe StatusCodes.OK
      entityAs[SyncResult] shouldBe SyncResult(
        syncedContacts = 0,
        contacts = List.empty,
      )
    }
  }

  it should "return 1 contact inserted when there's only 1 new contact" in {
    setContactsMockApi(List(contact1).asJson.toString())
    setContactsMailChimp(MemberList(List.empty, "", 0).asJson.toString())

    restClient.addResponse(
      HttpMethods.POST,
      s"/3.0/lists/${config.mailchimp.listIdToSync}/members",
      StatusCodes.OK,
      "",
    )

    val request = Get(uri = s"/${config.api.serviceName}/sync")
    request ~> syncRoutes ~> check {
      status shouldBe StatusCodes.OK
      entityAs[SyncResult] shouldBe SyncResult(
        syncedContacts = 1,
        contacts = List(contact1),
      )
    }
  }

  it should "return 1 contact inserted and 1 contact updated correctly" in {
    setContactsMockApi(List(contact1, contact2).asJson.toString())
    setContactsMailChimp(MemberList(List(member1), "", 1).asJson.toString())

    restClient.addResponse(
      HttpMethods.POST,
      s"/3.0/lists/${config.mailchimp.listIdToSync}/members",
      StatusCodes.OK,
      "",
    )
    restClient.addResponse(
      HttpMethods.PATCH,
      s"/3.0/lists/${config.mailchimp.listIdToSync}/members/${contact1.email}",
      StatusCodes.OK,
      "",
    )

    val request = Get(uri = s"/${config.api.serviceName}/sync")
    request ~> syncRoutes ~> check {
      status shouldBe StatusCodes.OK
      val response = entityAs[SyncResult]
      response.syncedContacts shouldBe 2
      response.contacts should contain theSameElementsAs List(contact1, contact2)
    }
  }

  it should "return 1 contact updated when there's only 1 contact in common" in {
    setContactsMockApi(List(contact1).asJson.toString())
    setContactsMailChimp(MemberList(List(member1), "", 1).asJson.toString())
    restClient.addResponse(
      HttpMethods.PATCH,
      s"/3.0/lists/${config.mailchimp.listIdToSync}/members/${contact1.email}",
      StatusCodes.OK,
      "",
    )

    val request = Get(uri = s"/${config.api.serviceName}/sync")
    request ~> syncRoutes ~> check {
      status shouldBe StatusCodes.OK
      entityAs[SyncResult] shouldBe SyncResult(
        syncedContacts = 1,
        contacts = List(contact1),
      )
    }
  }

  it should "return 1 contact updated with data updated" in {
    val contactToUpdate = contact1.copy(firstName = "aaa", lastName = "bbb")
    setContactsMockApi(List(contactToUpdate).asJson.toString())
    setContactsMailChimp(MemberList(List(member1), "", 1).asJson.toString())
    restClient.addResponse(
      HttpMethods.PATCH,
      s"/3.0/lists/${config.mailchimp.listIdToSync}/members/${contact1.email}",
      StatusCodes.OK,
      "",
    )

    val request = Get(uri = s"/${config.api.serviceName}/sync")
    request ~> syncRoutes ~> check {
      status shouldBe StatusCodes.OK
      entityAs[SyncResult] shouldBe SyncResult(
        syncedContacts = 1,
        contacts = List(contactToUpdate),
      )
    }
  }

  it should "return 1 contact deleted when there's 1 contact to delete in MailChimp" in {
    setContactsMockApi(List.empty[Contact].asJson.toString())
    setContactsMailChimp(MemberList(List(member1), "", 1).asJson.toString())
    restClient.addResponse(
      HttpMethods.POST,
      s"/3.0/lists/${config.mailchimp.listIdToSync}/members/${contact1.email}/actions/delete-permanent",
      StatusCodes.NoContent,
      "",
    )

    val request = Get(uri = s"/${config.api.serviceName}/sync")
    request ~> syncRoutes ~> check {
      status shouldBe StatusCodes.OK
      entityAs[SyncResult] shouldBe SyncResult(
        syncedContacts = 1,
        contacts = List(contact1),
      )
    }
  }

  it should "return 1 contact updated and 1 contact deleted" in {
    setContactsMockApi(List(contact1).asJson.toString())
    setContactsMailChimp(MemberList(List(member1, member2), "", 1).asJson.toString())

    restClient.addResponse(
      HttpMethods.PATCH,
      s"/3.0/lists/${config.mailchimp.listIdToSync}/members/${contact1.email}",
      StatusCodes.OK,
      "",
    )
    restClient.addResponse(
      HttpMethods.POST,
      s"/3.0/lists/${config.mailchimp.listIdToSync}/members/${contact2.email}/actions/delete-permanent",
      StatusCodes.NoContent,
      "",
    )

    val request = Get(uri = s"/${config.api.serviceName}/sync")
    request ~> syncRoutes ~> check {
      status shouldBe StatusCodes.OK
      val response = entityAs[SyncResult]
      response.syncedContacts shouldBe 2
      response.contacts should contain theSameElementsAs List(contact1, contact2)
    }
  }

  it should "return 1 contact updated, 1 contact updated and 1 contact deleted" in {
    setContactsMockApi(List(contact1, contact3).asJson.toString())
    setContactsMailChimp(MemberList(List(member1, member2), "", 1).asJson.toString())

    restClient.addResponse(
      HttpMethods.POST,
      s"/3.0/lists/${config.mailchimp.listIdToSync}/members",
      StatusCodes.OK,
      "",
    )
    restClient.addResponse(
      HttpMethods.PATCH,
      s"/3.0/lists/${config.mailchimp.listIdToSync}/members/${contact1.email}",
      StatusCodes.OK,
      "",
    )
    restClient.addResponse(
      HttpMethods.POST,
      s"/3.0/lists/${config.mailchimp.listIdToSync}/members/${contact2.email}/actions/delete-permanent",
      StatusCodes.NoContent,
      "",
    )

    val request = Get(uri = s"/${config.api.serviceName}/sync")
    request ~> syncRoutes ~> check {
      status shouldBe StatusCodes.OK
      val response = entityAs[SyncResult]
      response.syncedContacts shouldBe 3
      response.contacts should contain theSameElementsAs List(contact1, contact2, contact3)
    }
  }

  override def beforeEach(): Unit = {
    restClient.clearResponses()
    super.beforeEach()
  }

}
