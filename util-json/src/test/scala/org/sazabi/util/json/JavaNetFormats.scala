package org.sazabi.util.json
package formats

import com.twitter.finagle.core.util.InetAddressUtil
import com.twitter.finagle.util.InetSocketAddressUtil
import com.twitter.util.NetUtil

import java.net.{InetAddress, InetSocketAddress, URL}

import org.json4s._
import org.json4s.scalaz.JsonScalaz._

import org.scalatest._

import _root_.scalaz._

class JavaNetFormatsSpec extends FunSpec with Matchers with JavaNetFormats {
  val host = "127.0.0.1"
  val address = InetAddress.getByAddress(Array(127, 0, 0, 1))
  val ipAddress = NetUtil.ipToInt(host)

  val withPort = "127.0.0.1:8080"
  val socketAddress = new InetSocketAddress(address, 8080)

  val urlString = "https://github.com/solar/util"
  val url = new URL(urlString)

  describe("InetAddress") {
    describe("JSONR") {
      it("should convert JString to InetAddress") {
        fromJSON[InetAddress](JString(host)) should be (Success(address))
      }

      it("should convert JInt to InetAddress") {
        fromJSON[InetAddress](JInt(ipAddress)) should be (Success(address))
      }
    }

    describe("JSONW") {
      it("should convert InetAddress to JString") {
        toJSON(address) should be (JString(host))
      }
    }
  }

  describe("InetSocketAddress") {
    describe("JSONR") {
      it("should convert JString to InetSocketAddress") {
        fromJSON[InetSocketAddress](JString(withPort)) should be (
          Success(socketAddress))
      }

      it("should convert InetSocketAddress to JString") {
        toJSON(socketAddress) should be(JString(withPort))
      }
    }
  }

  describe("URL") {
    describe("JSONR") {
      it("should convert JString to URL") {
        fromJSON[URL](JString(urlString)) should be (Success(url))
      }

      it("should convert URL to JString") {
        toJSON(url) should be (JString(urlString))
      }
    }
  }
}
