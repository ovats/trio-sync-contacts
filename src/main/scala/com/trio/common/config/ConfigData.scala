package com.trio.common.config

object ConfigData {
  final case class ApiConfig(serviceName: String, host: String, port: String)

  final case class MailChimpConfig(
      apiKey: String,
      serverPrefix: String,
      url: String,
      listIdToSync: String,
      maxResultsPagination: Int,
  )

  final case class MockApiConfig(url: String)
}
