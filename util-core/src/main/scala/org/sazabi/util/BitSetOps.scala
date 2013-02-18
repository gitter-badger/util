package org.sazabi.util

import org.json4s.{JArray, JInt, JValue}

import org.sazabi.util.json.{Formats, Result}

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
  implicit lazy val bitSetJsonFormats: Formats[BitSet] = new Formats[BitSet] {
    def read(json: JValue): Result[BitSet] = json match {
      case JInt(n) => BitSet.fromBitMask(Array(n.longValue)).success
      case JArray(arr) => {
        Validation.fromTryCatch {
          BitSet.fromBitMask(
            arr.flatMap {
              case JInt(n) => List(n.longValue)
              case _ => List()
            }.toArray)
        }.swap.map(_.getMessage.wrapNel).swap
      }
      case _ => "Expected number value".failureNel
    }

    def write(v: BitSet): Result[JValue] = {
      JInt(new BitSetOps(v).toLong).success
    }
  }

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
