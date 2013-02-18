package org.sazabi.util.codec

import org.specs2._

import scalaz._

class Base58Spec extends Specification {
  def is =
    "Base58 encoding" ^ p^
      "Base58 should" ^
        "decode base58 encoded string to Array[Byte]" ! e1 ^
        "encode(decode(bs)) == bs"  ! e2 ^ end

  val bs: Base58String = Tag[String, Base58Encoded]("17wjHPRxwP5QYu2CJsRqNP6gbre7Uig36N")

  def e1 = {
    val bytes = Base58.decode(bs)
    bytes.isInstanceOf[Array[Byte]]
  }

  def e2 = {
    Base58.encode(Base58.decode(bs)) == bs
  }
}
