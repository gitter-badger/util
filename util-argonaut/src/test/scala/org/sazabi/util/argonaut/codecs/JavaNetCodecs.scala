package org.sazabi.util.argonaut
package codecs

import com.twitter.finagle.core.util.InetAddressUtil
import com.twitter.finagle.util.InetSocketAddressUtil
import com.twitter.util.NetUtil

import java.net.{ InetAddress, InetSocketAddress, URL }

import argonaut._
import argonaut.Json._
import argonaut.JsonIdentity._

import org.scalatest._

import _root_.scalaz._

class JavaNetCodecsSpec extends FunSpec with Matchers with JavaNetCodecs {
  val host = "127.0.0.1"
  val address = InetAddress.getByAddress(Array(127, 0, 0, 1))
  val ipAddress = NetUtil.ipToInt(host)

  val withPort = "127.0.0.1:8080"
  val socketAddress = new InetSocketAddress(address, 8080)

  val urlString = "https://github.com/solar/util"
  val url = new URL(urlString)

  private[this] def /(addr: InetAddress, port: Int) =
    new InetSocketAddress(addr, port)


  describe("InetAddress") {
    describe("DecodeJson") {
      it("should decode jString to InetAddress") {
        def test(arr: Int*) {
          jString(arr.mkString(".")).jdecode[InetAddress].value shouldBe Some(
            InetAddress.getByAddress(arr.map(_.toByte).toArray))
        }

        test(127, 0, 0, 1)
        test(192, 168, 13, 217)

        jString("google.com").jdecode[InetAddress].value should not be empty
        jString("test").jdecode[InetAddress].value shouldBe empty
      }

      it("should decode jNumber to InetAddress") {
        def test(v: Int*) {
          val i = NetUtil.ipToInt(v mkString ".")
          val addr = InetAddress.getByAddress(v.map(_.toByte).toArray)

          jNumber(i).jdecode[InetAddress].value shouldBe Some(addr)
        }

        test(127, 0, 0, 1)
        test(192, 168, 13, 217)
      }
    }

    describe("EncodeJson") {
      it("should encode InetAddress to jString") {
        def test(v: Int*) {
          val addr = InetAddress.getByAddress(v.map(_.toByte).toArray)
          val str = v.mkString(".")

          addr.asJson shouldBe jString(str)
        }
        test(127, 0, 0, 1)
        test(192, 168, 13, 217)
      }
    }
  }

  describe("InetSocketAddress") {
    describe("JSONR") {
      it("should decode jString to InetSocketAddress") {
        def test(port: Int, ip: Int*) {
          val str = s"${ip.mkString(".")}:$port"
          val addr = InetAddress.getByAddress(ip.map(_.toByte).toArray)
          val saddr = new InetSocketAddress(addr, port)

          jString(str).jdecode[InetSocketAddress].value shouldBe Some(saddr)
        }

        test(8080, 127, 0, 0, 1)
        test(80, 192, 168, 13, 217)

        jString("invalid saddr").jdecode[InetSocketAddress].value shouldBe empty
        jNumber(0d).jdecode[InetSocketAddress].value shouldBe empty
      }
    }
  }

  // describe("URL") {
    // describe("JSONR") {
      // it("should convert JString to URL") {
        // fromJSON[URL](JString(urlString)) should be (Success(url))
      // }

      // it("should convert URL to JString") {
        // toJSON(url) should be (JString(urlString))
      // }
    // }
  // }
}
