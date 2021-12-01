package com.trio.common

import com.trio.common.mailchimp.model.MemberDataRequest

import scala.concurrent.Future

trait MailService {

  def ping(): Future[ServiceResponse]

  def getListsInfo(): Future[ServiceResponse]

  def getListInfo(listId: String): Future[ServiceResponse]

  def getMembersOfList(listId: String): Future[ServiceResponse]

  def addMemberToList(listId: String, member: MemberDataRequest): Future[ServiceResponse]

  def updateMemberInList(listId: String, member: MemberDataRequest): Future[ServiceResponse]

  def permanentDeleteMemberInList(listId: String, email: String): Future[ServiceResponse]

}
