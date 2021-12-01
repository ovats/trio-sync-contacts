package com.trio.common.config

import com.trio.common.config.ConfigData.{ApiConfig, MailChimpConfig, MockApiConfig}
import com.typesafe.scalalogging.LazyLogging
import pureconfig.ConfigSource
import pureconfig.generic.auto._

final case class ApiAppConfig(
    api: ApiConfig,
    mailchimp: MailChimpConfig,
    mockapi: MockApiConfig,
)

object ApiAppConfig extends LazyLogging {

  def apply(resource: String = "application.conf"): ApiAppConfig = {

    ConfigSource.resources(resource).load[ApiAppConfig] match {
      case Left(errors) =>
        val msg = s"Unable to load service configuration (ApiAppConfig)"
        logger.error(
          s"$msg \n${errors.toList.map(_.description).mkString("* ", "\n*", "")}"
        )
        throw new IllegalStateException(msg)

      case Right(config) =>
        logger.debug(s"Successfully loaded configuration (ApiAppConfig), $config")
        config
    }

  }
}
