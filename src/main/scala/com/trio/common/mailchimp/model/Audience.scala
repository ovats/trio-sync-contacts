package com.trio.common.mailchimp.model

import io.circe.generic.extras.Configuration
import io.circe.generic.semiauto.deriveCodec
import io.circe.generic.extras.semiauto.deriveConfiguredCodec
import io.circe.Codec

import java.time.OffsetDateTime

final case class Contact(
    company: String,
    address1: String,
    address2: String,
    city: String,
    state: String,
    zip: String,
    country: String,
    phone: String,
)

object Contact {
  implicit val codec: Codec[Contact] = deriveCodec[Contact]
}

final case class Stats(
    memberCount: Int,
    unsubscribeCount: Int,
    cleanedCount: Int,
    memberCountSinceSend: Int,
    unsubscribeCountSinceSend: Int,
    cleanedCountSinceSend: Int,
    campaignCount: Int,
    campaignLastSent: String,
    mergeFieldCount: Int,
    avgSubRate: Int,
    avgUnsubRate: Int,
    targetSubRate: Int,
    openRate: Int,
    clickRate: Int,
    lastSubDate: OffsetDateTime,
)

object Stats {
  implicit val config: Configuration    = Configuration.default.withSnakeCaseMemberNames
  implicit val codecEvent: Codec[Stats] = deriveConfiguredCodec
}

final case class CampaignDefaults(fromName: String, fromEmail: String, subject: String, language: String)

object CampaignDefaults {
  implicit val config: Configuration               = Configuration.default.withSnakeCaseMemberNames
  implicit val codecEvent: Codec[CampaignDefaults] = deriveConfiguredCodec
}

final case class Audience(
    id: String,
    webId: Long,
    name: String,
    contact: Contact,
    permissionReminder: String,
    dateCreated: OffsetDateTime,
    stats: Stats,
    campaign_defaults: CampaignDefaults,
)

object Audience {
  implicit val config: Configuration       = Configuration.default.withSnakeCaseMemberNames
  implicit val codecEvent: Codec[Audience] = deriveConfiguredCodec
}

final case class AudienceList(lists: Seq[Audience])

object AudienceList {
  implicit val codec: Codec[AudienceList] = deriveCodec[AudienceList]
}
