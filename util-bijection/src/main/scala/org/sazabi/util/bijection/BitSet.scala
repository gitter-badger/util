package org.sazabi.util
package bijection

import com.twitter.bijection.{AbstractBijection, Bijection}

import scala.collection.immutable.BitSet

import scalaz._

trait BitSetBijections {
  implicit val bitMask2BitSet: Bijection[Array[Long], BitSet] =
    new AbstractBijection[Array[Long], BitSet] {
      def apply(n: Array[Long]): BitSet = BitSet.fromBitMask(n)

      override def invert(bits: BitSet): Array[Long] = bits.toBitMask
    }
}

object bitSet extends BitSetBijections
