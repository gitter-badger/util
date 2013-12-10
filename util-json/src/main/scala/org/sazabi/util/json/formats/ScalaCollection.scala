package org.sazabi.util
package json
package formats

import org.json4s.{JArray, JInt, JValue}
import org.json4s.scalaz.JsonScalaz._

import _root_.scala.collection.immutable.BitSet

import _root_.scalaz._
import syntax.id._
import syntax.validation._

trait ScalaCollectionFormats {
  /**
   * Implicit instance of org.json4s.scalaz.JsonScalaz.JSONR for BitSet.
   */
  implicit val bitSetJSONR: JSONR[BitSet] = Result2JSONR {
    case JInt(n) => BitSet.fromBitMask(Array(n.longValue)).success
    case JArray(arr) => {
      Validation.fromTryCatch {
        BitSet.fromBitMask(
          arr.flatMap {
            case JInt(n) => n.longValue :: Nil
            case _ => Nil
          }.toArray)
      }.swap.map(x => UncategorizedError("Invalid format", x.getMessage, Nil).wrapNel).swap
    }
    case j => UnexpectedJSONError(j, classOf[JInt]).failureNel
  }

  /**
   * Implicit instance of org.json4s.scalaz.JsonScalaz.JSONW for BitSet.
   */
  implicit val bitSetJSONW: JSONW[BitSet] = new JSONW[BitSet] with ToBitSetOps {
    def write(value: BitSet): JValue = {
      JInt(if (value.lastKey < 64) value.toLong else value.toBigInt)
    }
  }
}

object scalaCollection extends ScalaCollectionFormats
