package org.sazabi.util.json
package formats

import org.json4s.{JArray, JDecimal, JDouble, JInt, JValue}
import org.json4s.scalaz.JsonScalaz._

import _root_.scalaz._
import syntax.std.list._
import syntax.std.option._
import syntax.validation._

trait ScalazFormats {
  /**
   * Implicit instance of json4s-scalaz JSONR for scalaz.NonEmptyList.
   */
  implicit def nelJSONR[A : JSONR]: JSONR[NonEmptyList[A]] = Result2JSONR {
    case JArray(Nil) => UncategorizedError("Constraint violation",
      "NonEmptyList must contain at least an element", Nil).failNel
    case JArray(list) => {
      val results = list.map(fromJSON[A])
      val errs = results.flatMap {
        case Failure(nel) => nel.list
        case _ => Nil
      }
      if (errs.isEmpty) {
        results.collect {
          case Success(r) => r
        }.toNel.get.success
      } else errs.toNel.get.failure
    }
    case j => UnexpectedJSONError(j, classOf[JArray]).failNel
  }

  /**
   * Implicit isntance of json4s-scalaz JSONW for scalaz.NonEmptyList.
   */
  implicit def nelJSONW[A : JSONW]: JSONW[NonEmptyList[A]] = toJSONW {
    case nel => JArray(nel.list.map(toJSON(_)))
  }
}

object scalaz extends ScalazFormats
