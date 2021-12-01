package com.trio.common.mailchimp.model

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

final case class MergeFieldsRequest(FNAME: String, LNAME: String)

object MergeFieldsRequest {
  implicit val codec: Codec[MergeFieldsRequest] = deriveCodec[MergeFieldsRequest]
}

/**
 * Used for adding or updating a member in MailChimp.
 * These are the minimum fields required for that.
 */
final case class MemberDataRequest(
    email_address: String,
    status: String = "subscribed",
    merge_fields: MergeFieldsRequest,
)

object MemberDataRequest {
  implicit val codec: Codec[MemberDataRequest] = deriveCodec[MemberDataRequest]
}
