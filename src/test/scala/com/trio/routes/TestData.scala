package com.trio.routes

import com.trio.common.mailchimp.model.{Member, MergeFields}
import com.trio.common.mockapi.model.Contact

/**
 * This object contains data used in unit tests.
 */
object TestData {

  val contact1: Contact = Contact(firstName = "first1", lastName = "last", email = "mail1@mail.com")
  val contact2: Contact = Contact(firstName = "first2", lastName = "last", email = "mail2@mail.com")
  val contact3: Contact = Contact(firstName = "first3", lastName = "last", email = "mail3@mail.com")

  val member1: Member = Member(
    emailAddress = s"${contact1.email}",
    fullName = s"${contact1.firstName} ${contact1.lastName}",
    status = "",
    mergeFields = MergeFields(FNAME = contact1.firstName, LNAME = contact1.lastName),
  )

  val member2: Member = Member(
    emailAddress = s"${contact2.email}",
    fullName = s"${contact2.firstName} ${contact2.lastName}",
    status = "",
    mergeFields = MergeFields(FNAME = contact2.firstName, LNAME = contact2.lastName),
  )

  val member3: Member = Member(
    emailAddress = s"${contact3.email}",
    fullName = s"${contact3.firstName} ${contact3.lastName}",
    status = "",
    mergeFields = MergeFields(FNAME = contact3.firstName, LNAME = contact3.lastName),
  )

}
