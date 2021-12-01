package com.trio.common.services

import com.trio.common.ServiceResponse
import com.trio.common.mockapi.model.Contact

sealed trait SyncServiceResponse extends ServiceResponse

object SyncServiceResponse {

  final case class Error(message: String) extends SyncServiceResponse

  final case class SyncPerformed(contacts: Seq[Contact]) extends SyncServiceResponse

}
