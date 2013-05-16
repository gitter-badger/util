package org.sazabi.util.json
package formats

import org.json4s.{JArray, JDecimal, JDouble, JInt, JValue}
import org.json4s.scalaz.JsonScalaz._

import _root_.scalaz._
import syntax.std.list._
import syntax.std.option._
import syntax.validation._

trait ScalaFormats {
  /**
   * Implicit instance of json4s-scalaz JSONR for BigDecimal.
   */
  implicit object bigDecimalJSONR extends JSONR[BigDecimal] {
    def read(json: JValue): Result[BigDecimal] = json match {
      case JInt(i) => BigDecimal(i).success
      case JDouble(d) => BigDecimal(d).success
      case JDecimal(d) => d.success
      case j => UnexpectedJSONError(j, classOf[JDecimal]).failureNel
    }
  }

  /**
   * Implicit instance of json4s-scalaz JSONW for BigDecimal.
   */
  implicit object bigDecimalJSONW extends JSONW[BigDecimal] {
    def write(value: BigDecimal): JValue = JDecimal(value)
  }

  /**
   * Implicit instance of json4s-scalaz JSONR for Seq.
   */
  implicit def seqJSONR[A : JSONR]: JSONR[Seq[A]] = Result2JSONR {
    case JArray(list) => {
      val results = list.map(fromJSON[A])
      val errs = results.flatMap {
        case Failure(nel) => nel.list
        case _ => Nil
      }
      if (errs.isEmpty) {
        results.collect {
          case Success(r) => r
        }.success
      } else errs.toNel.get.failure
    }
    case j => UnexpectedJSONError(j, classOf[JArray]).failureNel
  }

  /**
   * Implicit instance of json4s-scalaz JSONW for Seq.
   */
  implicit def seqJSONW[A : JSONW]: JSONW[Seq[A]] = toJSONW { case seq =>
    JArray(seq.map(toJSON(_)).toList)
  }
}

object scala extends ScalaFormats
