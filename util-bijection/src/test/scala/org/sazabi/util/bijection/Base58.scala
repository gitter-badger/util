package org.sazabi.util.bijection

import com.twitter.bijection.{Tag => _, _}

import org.specs2._

import scalaz._

class Base58Spec extends Specification with Bijections {
  def is =
    "Base58 encoding" ^ p^
      "Base58 should" ^
        "valid base58 string --> Base58String" ! validBase58String ^
        "convert invalid base58 string --> Base58String" ! invalidBase58String ^
        "Base58String -->  Array[Byte]" ! base58String2Bytes ^
        "Array[Byte] --> Base58String" ! bytes2Base58String ^
        end

  val valid = "17wjHPRxwP5QYu2CJsRqNP6gbre7Uig36N"
  val invalid = "43290adfqOweoalalsdjfaqeowaur9324I"

  def validBase58String = {
    Injection.invert[Base58String, String](valid).isDefined
  }

  def invalidBase58String = {
    Injection.invert[Base58String, String](invalid).isEmpty
  }

  def base58String2Bytes = {
    val base58 = Injection.invert[Base58String, String](valid).get
    val bytes = Bijection[Base58String, Array[Byte]](base58)
    bytes must beAnInstanceOf[Array[Byte]]
  }

  def bytes2Base58String = {
    val base58 = Injection.invert[Base58String, String](valid).get
    val bytes = Bijection[Base58String, Array[Byte]](base58)
    val reversed = Bijection[Array[Byte], Base58String](bytes)

    base58 must_== reversed
  }
}
