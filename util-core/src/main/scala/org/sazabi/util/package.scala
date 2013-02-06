package org.sazabi

import scalaz._

/**
 * Finagle HTTP server for APIs of the Casino platform.
 */
package object util {
  type -->[A, B] = PartialFunction[A, B]

  private[util] sealed trait Base58Encoded
  type Base58String = String @@ Base58Encoded
}
