package org.sazabi.util.codec

import com.twitter.util.Codec

import scala.annotation.tailrec
import scala.math._
import scala.collection.mutable.StringBuilder

import scalaz._
import Scalaz._

object Base58 extends Codec[Array[Byte], Base58String] {
  private val Base58Chars =
    "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"

  private val Base58Size = Base58Chars.size

  def validate(str: String): \/[Throwable, Base58String] = \/.fromTryCatch {
    val bs = Tag[String, Base58Encoded](str)
    encode(decode(bs))
  }

  override def encode(bytes: Array[Byte]): Base58String = {
    val bi = BigInt(1, bytes)

    val s = new StringBuilder

    @tailrec
    def append(rest: BigInt) {
      val div = rest / Base58Size
      val mod = rest % Base58Size
      s.insert(0, Base58Chars(mod.intValue))
      if (div > 0) append(div)
    }

    append(bi)

    val zeros = bytes.indexWhere(_ != 0)
    0 until zeros foreach { _ => s.insert(0, Base58Chars(0)) }

    Tag[String, Base58Encoded](s.toString)
  }

  override def decode(bs: Base58String): Array[Byte] = {
    val seq = 0 until bs.size map { i =>
      val index = Base58Chars.indexOf(bs(i))
      if (index === -1) throw new IllegalArgumentException(
        "An invalid character (%c) at index %d".format(bs(i), i))
      BigInt(index) * BigInt(Base58Size).pow(bs.size - 1 - i)
    }

    val bytes = seq.sum.toByteArray

    val offset = (bytes.size > 2 && bytes(0) === 0 && bytes(1) < 0) ? 1 | 0;

    val zeros = bs.indexWhere(_ =/= Base58Chars.head)

    val dest = new Array[Byte](bytes.size - offset + zeros)

    Array.copy(bytes, offset, dest, zeros, dest.size - zeros)
    dest
  }
}
