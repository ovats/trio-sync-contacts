package com.trio

package object common {

  def makeUrl(base: String, uri: String): String = {
    if (base.trim.endsWith("/"))
      s"${base.trim}$uri"
    else
      s"${base.trim}/$uri"
  }

}
