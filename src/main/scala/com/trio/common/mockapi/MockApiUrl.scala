package com.trio.common.mockapi

import com.trio.common.makeUrl

object MockApiUrl {
  def getContactsUrl(baseUrl: String): String = makeUrl(baseUrl, "contacts")
}
