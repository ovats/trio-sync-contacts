package com.trio.common.mailchimp

import com.trio.common.ServiceResponse
import com.trio.common.mailchimp.model.{Audience, AudienceList, Member, MemberList, MergeFields}

sealed trait MailChimpResponse extends ServiceResponse

object MailChimpResponse {

  final case class MemberAdded(email: String)            extends MailChimpResponse
  final case class MemberUpdated(email: String)          extends MailChimpResponse
  final case class MemberPermanentDeleted(email: String) extends MailChimpResponse

  final case class AudienceListRetrieved(audiences: AudienceList) extends MailChimpResponse
  final case class AudienceRetrieved(audience: Audience)          extends MailChimpResponse

  final case class MembersListRetrieved(members: MemberList) extends MailChimpResponse
  final case class MemberRetrieved(member: Member)           extends MailChimpResponse

  final case class Error(message: String) extends MailChimpResponse

  final case object PingSuccessfully extends MailChimpResponse
  final case object PingError        extends MailChimpResponse

}
