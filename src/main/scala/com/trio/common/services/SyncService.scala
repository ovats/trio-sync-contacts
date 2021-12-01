package com.trio.common.services

import com.trio.common.config.ApiAppConfig
import com.trio.common.mailchimp.{MailChimpClient, MailChimpResponse}
import com.trio.common.mailchimp.MailChimpResponse.MembersListRetrieved
import com.trio.common.mailchimp.model.{Member, MemberDataRequest, MergeFieldsRequest}
import com.trio.common.mockapi.{MockApiClient, MockApiResponse}
import com.trio.common.mockapi.model.Contact
import com.trio.common.services.SyncServiceResponse.SyncPerformed
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{ExecutionContext, Future}

class SyncService(mailChimpClient: MailChimpClient, mockApiClient: MockApiClient, config: ApiAppConfig)(implicit
    ec: ExecutionContext
) extends LazyLogging {

  /**
   * Calculates what needs to be done in MailChimp.
   *
   * @param chimp implementation of MailChimp client
   * @param mock implementation of MockAPi client
   * @return return the following lists:
   *  - which contacts needs to be added in MailChimp
   *  - which contacts needs to be updated in MailChimp
   *  - which contacts needs to be delete in MailChimp
   */
  private def calculateChanges(
      chimp: MailChimpResponse,
      mock: MockApiResponse,
  ): (Seq[Contact], Seq[Contact], Seq[Member]) = {

    val contactsFromMockApi: Seq[Contact] = mock match {
      case MockApiResponse.ContactsRetrieved(contacts) => contacts
      case _                                           => List.empty
    }

    val membersFromMailChimp = chimp match {
      case MembersListRetrieved(members) => members.members
      case _                             => List.empty
    }

    // Contacts that exists in MockApi but doesn't exists in MailChimp
    // => to be created in MailChimp
    val newContacts = contactsFromMockApi.filter { c =>
      !membersFromMailChimp.exists(m => m.emailAddress == c.email)
    }

    // Contacts that exists in both services (MockAPI, MailChimp)
    // => to be updated in MailChimp
    val matchContacts = contactsFromMockApi.filter { c =>
      membersFromMailChimp.exists(m => m.emailAddress == c.email)
    }

    // Contacts that only exists in MailChimp
    // => to be deleted in MailChimp
    val deleteContacts = membersFromMailChimp.filter { m =>
      !contactsFromMockApi.exists(c => c.email == m.emailAddress)
    }

    logger.debug(s"New contacts: ${newContacts.map(_.email)}")
    logger.debug(s"Matched contacts: ${matchContacts.map(_.email)}")
    logger.debug(s"Deleted contacts: ${deleteContacts.map(_.emailAddress)}")

    (newContacts, matchContacts, deleteContacts)
  }

  /**
   * Add contacts in MailChimp
   *
   * @param newContacts contacts that only exists in MockApi.
   * @return
   */
  private def addMembers(newContacts: Seq[Contact]) = {
    val results = newContacts.map { contact =>
      val newMember = MemberDataRequest(
        email_address = contact.email,
        merge_fields = MergeFieldsRequest(FNAME = contact.firstName, LNAME = contact.lastName),
      )
      mailChimpClient.addMemberToList(config.mailchimp.listIdToSync, newMember)
    }
    Future.sequence(results)
  }

  /**
   * Update contacts MailChimp.
   *
   * @param matchContacts contacts that exists in MockAPI and MailChimp at the same time.
   * @return
   */

  private def updateMembers(matchContacts: Seq[Contact]) = {
    val updateResult = matchContacts.map { contact =>
      val memberToUpdate = MemberDataRequest(
        email_address = contact.email,
        merge_fields = MergeFieldsRequest(FNAME = contact.firstName, LNAME = contact.lastName),
      )
      mailChimpClient.updateMemberInList(config.mailchimp.listIdToSync, memberToUpdate)
    }
    Future.sequence(updateResult)
  }

  /**
   * Permanent delete contacts in MailChimp.
   *
   * Note: if a contact (given by its email has been deleted), then it can be added
   *       again using addMembers. It's a special case not handled in this implementation.
   *
   * @param deleteContacts contacts that only exists in MailChimp
   * @return
   */
  private def deleteMembers(deleteContacts: Seq[Member]) = {
    val deleteResult = deleteContacts.map(contact =>
      mailChimpClient.permanentDeleteMemberInList(config.mailchimp.listIdToSync, contact.emailAddress)
    )
    Future.sequence(deleteResult)
  }

  private def getContactsSync(
      newContacts: Seq[Contact],
      insertsSuccessfully: Seq[String],
      matchContacts: Seq[Contact],
      updatesSuccessfully: Seq[String],
      deleteContacts: Seq[Member],
      deletesSuccessfully: Seq[String],
  ): Seq[Contact] = {

    val i = insertsSuccessfully
      .foldLeft(List.empty[Option[Contact]]) { (acc, email) =>
        acc ++ List(newContacts.find(c => c.email == email))
      }
      .flatten

    val u = updatesSuccessfully
      .foldLeft(List.empty[Option[Contact]]) { (acc, email) =>
        acc ++ List(matchContacts.find(c => c.email == email))
      }
      .flatten

    val d = deletesSuccessfully
      .foldLeft(List.empty[Option[Contact]]) { (acc, email) =>
        acc ++ List(deleteContacts.find(c => c.emailAddress == email).map { m =>
          Contact(
            firstName = m.mergeFields.FNAME,
            lastName = m.mergeFields.LNAME,
            email = m.emailAddress,
          )
        })
      }
      .flatten

    i ++ u ++ d
  }

  /**
   * Sync contacts between MockApi and MailChimp
   *
   * @return
   */
  def syncContacts(): Future[SyncServiceResponse] = {

    val changesToDo: Future[(Seq[Contact], Seq[Contact], Seq[Member])] = for {
      chimp <- mailChimpClient.getMembersOfList(config.mailchimp.listIdToSync)
      mock  <- mockApiClient.getContacts()
    } yield {
      val (newContacts, matchContacts, deleteContacts) = calculateChanges(chimp, mock)
      (newContacts, matchContacts, deleteContacts)
    }

    changesToDo.flatMap { changes =>
      for {
        insertsPerformed <- addMembers(changes._1)
        updatesPerformed <- updateMembers(changes._2)
        deletedPerformed <- deleteMembers(changes._3)
      } yield {

        logger.debug(s"Insert results: $insertsPerformed")
        logger.debug(s"Update results: $updatesPerformed")
        logger.debug(s"Delete results: $deletedPerformed")

        val insertsSuccessfully = insertsPerformed.collect { case a: MailChimpResponse.MemberAdded => a }
        val updatesSuccessfully = updatesPerformed.collect { case a: MailChimpResponse.MemberUpdated => a }
        val deletesSuccessfully = deletedPerformed.collect { case a: MailChimpResponse.MemberPermanentDeleted => a }

        SyncPerformed(
          contacts = getContactsSync(
            newContacts = changes._1,
            insertsSuccessfully = insertsSuccessfully.map(_.email),
            matchContacts = changes._2,
            updatesSuccessfully = updatesSuccessfully.map(_.email),
            deleteContacts = changes._3,
            deletesSuccessfully = deletesSuccessfully.map(_.email),
          )
        )

      }
    }

  }

}
