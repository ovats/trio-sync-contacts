package com.trio.common.mailchimp.model

import io.circe.Codec
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.deriveConfiguredCodec
import io.circe.generic.semiauto.deriveCodec

/**
 * This is part of the data of each member in MailChimp.
 * FNAME and LNAME are used to set field full_name in MailChimp.
 */
final case class MergeFields(FNAME: String, LNAME: String)

object MergeFields {
  implicit val codec: Codec[MergeFields] = deriveCodec[MergeFields]
}

/**
 * This case class MemberData represent each member/contact of
 * some list/audience (list/audience is modelled in ListData CC)
 */
final case class Member(
    emailAddress: String,
    fullName: String,
    status: String,
    mergeFields: MergeFields,
)

object Member {
  implicit val config: Configuration     = Configuration.default.withSnakeCaseMemberNames
  implicit val codecEvent: Codec[Member] = deriveConfiguredCodec
}

final case class MemberList(members: Seq[Member], listId: String, totalItems: Long)

object MemberList {
  implicit val config: Configuration         = Configuration.default.withSnakeCaseMemberNames
  implicit val codecEvent: Codec[MemberList] = deriveConfiguredCodec
}
