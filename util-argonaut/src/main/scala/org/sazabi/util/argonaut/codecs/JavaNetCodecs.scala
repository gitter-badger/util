package org.sazabi.util.argonaut
package codecs

import argonaut._
import argonaut.Json._
import argonaut.DecodeJson._

import com.twitter.finagle.core.util.InetAddressUtil
import com.twitter.finagle.util.InetSocketAddressUtil
import com.twitter.util.NetUtil

import java.net.{ InetAddress, InetSocketAddress, UnknownHostException, URL }

import scala.util.{ Failure, Success, Try }

trait JavaNetCodecs {
  private[this] def doubleToInetAddress(d: Double, a: HCursor):
      DecodeResult[InetAddress] = {
    Try(d.toInt) flatMap { i =>
      val bytes = Array[Byte](
        ((i & 0xff000000) >> 24).toByte,
        ((i & 0x00ff0000) >> 16).toByte,
        ((i & 0x0000ff00) >>  8).toByte,
        ((i & 0x000000ff)).toByte)

      Try(InetAddress.getByAddress(bytes))
    } match {
      case Success(addr) => DecodeResult.ok(addr)
      case Failure(e: UnknownHostException) =>
        DecodeResult.fail(e.getMessage, a.history)
      case _ =>
        DecodeResult.fail("Invalid json for InetAddress", a.history)
    }
  }

  private[this] def stringToInetAddress(s: String, a: HCursor):
      DecodeResult[InetAddress] = {
    Try(InetAddressUtil.getByName(s)) match {
      case Success(addr) => DecodeResult.ok(addr)
      case Failure(e: UnknownHostException) =>
        DecodeResult.fail(e.getMessage, a.history)
      case Failure(_) =>
        DecodeResult.fail("Invalid json for InetAddress", a.history)
    }
  }

  implicit def InetAddressDecodeJson: DecodeJson[InetAddress] =
    DecodeJson(a => a.focus.number orElse a.focus.string match {
      case Some(d: Double) => doubleToInetAddress(d, a)
      case Some(s: String) => stringToInetAddress(s, a)
      case _ => DecodeResult.fail("Invalid json for InetAddress", a.history)
    })

  implicit def InetAddressEncodeJson: EncodeJson[InetAddress] =
    EncodeJson { a =>
      val str = a.toString
      jString(str.drop(str.indexOf('/') + 1))
    }

  implicit def InetSocketAddressDecodeJson: DecodeJson[InetSocketAddress] =
    DecodeJson(a => a.focus.string.flatMap { host =>
      Try(InetSocketAddressUtil.parseHosts(host).head).toOption
    } match {
      case Some(addr) => DecodeResult.ok(addr)
      case _ =>
        DecodeResult.fail("Invalid json for InetSocketAddress", a.history)
    })

  implicit def InetSocketAddressEncodeJson: EncodeJson[InetSocketAddress] =
    EncodeJson { a =>
      val str = a.toString
      jString(str.drop(str.indexOf('/') + 1))
    }
}
