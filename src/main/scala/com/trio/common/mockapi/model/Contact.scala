package com.trio.common.mockapi.model

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

/**
 * Typical contact:
 *   {
 *    "createdAt":"2021-09-10T11:25:43.489Z",
 *    "firstName":"Mohammed",
 *    "lastName":"Koepp",
 *    "email":"Augustine62@gmail.com",
 *    "avatar":"https://cdn.fakercloud.com/avatars/ddggccaa_128.jpg",
 *    "id":"1"
 *   },
 */

/**
 * In our domain we only need 3 fields.
 */
final case class Contact(
    firstName: String,
    lastName: String,
    email: String,
)

object Contact {
  implicit val codec: Codec[Contact] = deriveCodec[Contact]
}
