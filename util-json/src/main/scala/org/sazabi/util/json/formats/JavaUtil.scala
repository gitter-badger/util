package org.sazabi.util.json
package formats

import java.util.UUID

import org.json4s.JString
import org.json4s.scalaz.JsonScalaz._

import _root_.scalaz._
import syntax.id._
import syntax.validation._

trait JavaUtilFormats {
  /**
   * Implicit JSONR instance for UUID.
   */
  implicit val uuidJSONR: JSONR[UUID] = Result2JSONR {
    case JString(str) => Validation.fromTryCatch(UUID.fromString(str)).swap
      .map(x => UncategorizedError(
        "Invalid UUID format", x.getMessage, Nil).wrapNel)
      .swap
    case j => UnexpectedJSONError(j, classOf[JString]).failNel
  }

  /**
   * Implicit JSONW instance for UUID.
   */
  implicit def uuidJSONW: JSONW[UUID] = toJSONW { case value =>
    JString(value.toString)
  }
}

object javaUtil extends JavaUtilFormats
