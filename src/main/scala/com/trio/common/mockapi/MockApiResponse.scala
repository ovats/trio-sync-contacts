package com.trio.common.mockapi

import com.trio.common.ServiceResponse
import com.trio.common.mockapi.model.Contact

sealed trait MockApiResponse extends ServiceResponse

object MockApiResponse {

  final case class ContactsRetrieved(contacts: Seq[Contact]) extends MockApiResponse

  final case class Error(message: String) extends MockApiResponse

}
