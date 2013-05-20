package org.sazabi.util.bijection

import com.twitter.bijection.{Tag => _, _}
import com.twitter.bijection.Conversion.asMethod

import org.apache.commons.codec.binary.Hex

import scala.annotation.tailrec
import scala.math._
import scala.collection.mutable.StringBuilder

import scalaz._
import std.anyVal._
import std.string._
import syntax.order._
import syntax.std.boolean._

case class HexString private[bijection](str: String)

trait HexBijections {
  implicit val hexString2String: Injection[HexString, String] =
    new AbstractInjection[HexString, String] {
      def apply(hex: HexString): String = hex.str

      override def invert(str: String): Option[HexString] = {
        try {
          Hex.decodeHex(str.toArray)
          Some(HexString(str))
        } catch { case e: Throwable =>
          logger.warning("Injection.invert[HexString, String]() failed: " +
            e.getMessage)
          None
        }
      }
    }

  implicit val bytes2HexString: Bijection[Array[Byte], HexString] =
    new AbstractBijection[Array[Byte], HexString] {
      def apply(bytes: Array[Byte]): HexString =
        HexString(Hex.encodeHex(bytes).mkString)

      override def invert(hex: HexString): Array[Byte] =
        Hex.decodeHex(hex.str.toArray)
    }
}

object hex extends HexBijections
