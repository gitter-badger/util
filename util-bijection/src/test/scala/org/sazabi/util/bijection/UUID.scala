package org.sazabi.util.bijection

import com.twitter.bijection.{Tag => _, _}

import java.util.UUID

import org.specs2._

import scalaz._

class UUIDSpec extends Specification with Bijections {
  def is =
    "UUID Injections" ^ p^
      "Injection[UUID, Array[Byte]]" ^
        "UUID --> Array[Byte]" ! uuid2Bytes ^
        "valid Array[Byte] --> UUID" ! validBytes2UUID ^
        "invalid Array[Byte] --> UUID" ! invalidBytes2UUID ^
        end ^
      "Injection[UUID, String]" ^
        "UUID --> String" ! uuid2String ^
        "valid String --> UUID" ! validString2UUID ^
        "invalid String --> UUID" ! invalidString2UUID ^
        end

  val validStr = "550e8400-e29b-41d4-a716-446655440000"
  val validBytes = Array[Byte](85, 14, -124, 0, -30, -101, 65, -44,
    -89, 22, 68, 102, 85, 68, 0, 0)

  val uuid = UUID.fromString(validStr)

  val invalidBytes = new Array[Byte](12)
  val invalidStr = "fea9047320483290f4j2q3efq2309482t690"

  def uuid2Bytes(implicit inj: Injection[UUID, Array[Byte]]) = {
    val bytes = inj(uuid)
    bytes must_== validBytes
  }

  def validBytes2UUID(implicit inj: Injection[UUID, Array[Byte]]) = {
    val u = inj.invert(validBytes)
    u must beSome like {
      case Some(u) => u must_== uuid
    }
  }

  def invalidBytes2UUID(implicit inj: Injection[UUID, Array[Byte]]) = {
    inj.invert(invalidBytes) must beNone
  }

  def uuid2String(implicit inj: Injection[UUID, String]) = {
    val str = inj(uuid)
    str must_== validStr
  }

  def validString2UUID(implicit inj: Injection[UUID, String]) = {
    val u = inj.invert(validStr)
    u must beSome like {
      case Some(u) => u must_== uuid
    }
  }

  def invalidString2UUID(implicit inj: Injection[UUID, String]) = {
    inj.invert(invalidStr) must beNone
  }
}
