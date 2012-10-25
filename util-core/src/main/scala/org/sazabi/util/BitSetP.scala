package org.sazabi.util

import net.liftweb.json.{JInt, JValue}

import org.sazabi.util.json.{Formats, Result}

import scala.collection.immutable.BitSet

import scalaz._
import std.anyVal._
import syntax.equal._
import syntax.std.boolean._
import syntax.validation._

/**
 * A pimped trait for BitSet.
 */
trait BitSetP extends Pimped[BitSet] {
  def toInt: Int = value.foldLeft(0)((x, y) => x | (value(y) ? 1 | 0) << y)
  def toLong: Long = value.foldLeft(0L)((x, y) => x | (value(y) ? 1L | 0L) << y)
}

trait BitSets {
  implicit def toBitSetP(v: BitSet): BitSetP = new BitSetP {
    val value = v
  }

  implicit lazy val bitSetFormat: Formats[BitSet] = new Formats[BitSet] {
    def read(json: JValue): Result[BitSet] = json match {
      case JInt(n) => BitSet.fromArray(Array(n.longValue)).success
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
