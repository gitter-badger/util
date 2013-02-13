package org.sazabi.util

import scalaz._

package object codec {
  private[codec] sealed trait Base58Encoded
  type Base58String = String @@ Base58Encoded
}
