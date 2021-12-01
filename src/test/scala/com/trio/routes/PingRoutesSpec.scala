package com.trio.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.trio.api.routes.PingRoutes
import com.trio.common.config.ApiAppConfig
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers

class PingRoutesSpec extends AnyFlatSpecLike with Matchers with ScalatestRouteTest {

  private val config     = ApiAppConfig()
  private val pingRoutes = new PingRoutes(config).routes

  s"GET /${config.api.serviceName}/ping" should "return pong" in {
    val request = Get(uri = s"/${config.api.serviceName}/ping")
    request ~> pingRoutes ~> check {
      status shouldBe StatusCodes.OK
      entityAs[String] shouldBe "pong"
    }
  }
}
