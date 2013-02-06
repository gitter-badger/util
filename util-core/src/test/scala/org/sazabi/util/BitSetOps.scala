package org.sazabi.util

import org.specs2._

import scala.collection.immutable.BitSet

import scalaz._

class BitSetOpsSpec extends Specification with BitSets {
  def is =
    "Implicit class BitSetOps implements operations for BitSet"   ^
                                                                  p^
      "BitSetOps should"                                          ^
        "be generated from BitSet"                                ! e1^
        "be able to convert to Int"                               ! e2^
                                                                  end

  val bitset = BitSet(0, 2, 3)

  def e1 = {
    val ops: BitSetOps = bitset
    ops.isInstanceOf[BitSetOps]
  }

  def e2 = {
    bitset.toInt == 13
  }
}
