package org.sazabi.util.bijection

import com.twitter.bijection.{Bijection, Injection}

import org.specs2._

import scalaz._

class Base58Spec extends Specification with Bijections {
  def is =
    "Base58 bijections" ^
      "Base58String -->  Array[Byte]" ! base58String2Bytes ^
      "Array[Byte] --> Base58String" ! bytes2Base58String ^
      end ^
    "Base58 injections" ^
      "valid base58 string <-> Base58String" ! validBase58String ^
      "invalid base58 string --> Base58String" ! invalidBase58String ^
      end

  val valid = "17wjHPRxwP5QYu2CJsRqNP6gbre7Uig36N"
  val base58 = Base58String(valid)

  val invalid = "43290adfqOweoalalsdjfaqeowaur9324I"

  def base58String2Bytes(implicit bij: Bijection[Array[Byte], Base58String]) = {
    val bytes = bij.invert(base58)
    bytes must beAnInstanceOf[Array[Byte]]
  }

  def bytes2Base58String(implicit bij: Bijection[Array[Byte], Base58String]) = {
    val bytes = bij.invert(base58)
    val reversed = bij(bytes)

    base58 must_== reversed
  }

  def validBase58String(implicit inj: Injection[Base58String, String]) = {
    val opt = inj.invert(valid)
    opt must beSome like {
      case Some(base58) => inj(base58) must_== valid
    }
  }

  def invalidBase58String(implicit inj: Injection[Base58String, String]) = {
    inj.invert(invalid) must beNone
  }
}
