package org.sazabi.util

import org.json4s.{JArray, JInt, JValue}
import org.json4s.scalaz.JsonScalaz._

import scala.collection.immutable.BitSet

import scalaz._
import std.anyVal._
import syntax.equal._
import syntax.id._
import syntax.std.boolean._
import syntax.validation._

class BitSetOps(val value: BitSet) extends AnyVal {
  def toInt: Int = value.foldLeft(0)((x, y) => x | (value(y) ? 1 | 0) << y)
  def toLong: Long = value.foldLeft(0L)((x, y) => x | (value(y) ? 1L | 0L) << y)
}

trait ToBitSetOps {
  implicit val toBitSetOps: BitSet => BitSetOps = new BitSetOps(_)
}

/**
 * A pimped trait for BitSet.
 */
trait BitSetTypeClasses {
  /**
   * Implicit instance of org.json4s.scalaz.JsonScalaz.JSONR.
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
   * Implicit instance of org.json4s.scalaz.JsonScalaz.JSONW.
   */
  implicit val bitSetJSONW: JSONW[BitSet] = new JSONW[BitSet] with ToBitSetOps {
    def write(value: BitSet): JValue = JInt(value.toLong)
  }

  /**
   * Implicit instance of scalaz type classes.
   */
  implicit lazy val bitSetScalazInstance:
    Monoid[BitSet] with Show[BitSet] with Order[BitSet] =
      new Monoid[BitSet] with Show[BitSet] with Order[BitSet] {
    def zero: BitSet = BitSet.empty

    def append(f1: BitSet, f2: => BitSet): BitSet = f1 ++ f2

    override def show(f: BitSet): Cord = Cord(f mkString ",")

    def order(f1: BitSet, f2: BitSet): Ordering =
      Order[Long].order(new BitSetOps(f1).toLong, new BitSetOps(f2).toLong)
  }
}
