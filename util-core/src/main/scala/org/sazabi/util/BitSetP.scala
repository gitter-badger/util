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

/**
 * A pimped trait for BitSet.
 */
trait BitSets {
  implicit class BitSetOps(self: BitSet) {
    def toInt: Int = self.foldLeft(0)((x, y) => x | (self(y) ? 1 | 0) << y)
    def toLong: Long = self.foldLeft(0L)((x, y) => x | (self(y) ? 1L | 0L) << y)
  }

  implicit lazy val bitSetFormat: Formats[BitSet] = new Formats[BitSet] {
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
      JInt(v.toLong).success
    }
  }

  implicit lazy val bitSetInstance: Monoid[BitSet] with Show[BitSet] with Equal[BitSet] =
      new Monoid[BitSet] with Show[BitSet] with Equal[BitSet] {
    def zero: BitSet = BitSet.empty

    def append(f1: BitSet, f2: => BitSet): BitSet = f1 ++ f2

    override def show(f: BitSet): Cord = Cord(f mkString ",")

    def equal(f1: BitSet, f2: BitSet): Boolean = f1.toLong === f2.toLong
  }
}
