package org.sazabi.util

import scalaz._

package object codec {
  private[codec] sealed trait Base58Encoded
  private[codec] sealed trait Base64Encoded

  type Base58String = String @@ Base58Encoded
  type Base64String = String @@ Base64Encoded
}
