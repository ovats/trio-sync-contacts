package com.trio.common

import scala.concurrent.Future

trait ContactsService {

  def getContacts(): Future[ServiceResponse]

}
