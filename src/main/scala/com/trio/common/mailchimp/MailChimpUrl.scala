package com.trio.common.mailchimp

import com.trio.common.makeUrl

object MailChimpUrl {

  // Urls related to lists/audience
  def pingUrl(baseUrl: String): String = makeUrl(baseUrl, "ping")

  def getListsInfoUrl(baseUrl: String, count: Int = 10, offset: Int = 0): String =
    makeUrl(baseUrl, s"lists?count=$count&offset=$offset")

  def getListInfoUrl(baseUrl: String, listId: String): String = makeUrl(baseUrl, s"lists/$listId")

  // Urls related to members of lists/audience
  def getMembersOfListUrl(baseUrl: String, listId: String, count: Int = 10, offset: Int = 0): String =
    makeUrl(baseUrl, s"lists/$listId/members?count=$count&offset=$offset")

  def addMemberUrl(baseUrl: String, listId: String): String =
    makeUrl(baseUrl, s"lists/$listId/members")

  def updateMemberUrl(baseUrl: String, listId: String, email: String): String =
    makeUrl(baseUrl, s"lists/$listId/members/$email")

  def permanentDeleteMemberUrl(baseUrl: String, listId: String, email: String): String =
    makeUrl(baseUrl, s"lists/$listId/members/$email/actions/delete-permanent")

}
