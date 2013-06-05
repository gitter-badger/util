package org.sazabi.util.json
package formats

import com.twitter.finagle.core.util.InetAddressUtil
import com.twitter.finagle.util.InetSocketAddressUtil
import com.twitter.util.NetUtil

import java.net.{InetAddress, InetSocketAddress, URL}

import org.json4s._
import org.json4s.scalaz.JsonScalaz._

import org.specs2._

import _root_.scalaz._

class JavaNetFormatsSpec extends Specification with JavaNetFormats { def is = s2"""
  InetAddress
    JSONR[InetAddress] should
      convert JString to InetAddress      $JStringToInetAddress
      convert JInt to InetAddress         $JIntToInetAddress

    JSONW[InetAddress] should
      convert InetAddress to JString      $InetAddressToJString

  InetSocketAddress
    JSONR[InetSocketAddress] should
      convert JString to InetSocketAddress  $JStringToInetSocketAddress

    JSONW[InetSocketAddress] should
      convert InetSocketAddress to JString  $InetSocketAddressToJString

  URL
    JSONR[URL] should
      convert JString to URL  $JStringToURL

    JSONW[URL] should
      convert URL to JString  $URLToJString"""

  val host = "127.0.0.1"
  val address = InetAddress.getByAddress(Array(127, 0, 0, 1))
  val ipAddress = NetUtil.ipToInt(host)

  val withPort = "127.0.0.1:8080"
  val socketAddress = new InetSocketAddress(address, 8080)

  val urlString = "https://github.com/solar/util"
  val url = new URL(urlString)

  def JStringToInetAddress = {
    fromJSON[InetAddress](JString(host)) must_== Success(address)
  }

  def JIntToInetAddress = {
    fromJSON[InetAddress](JInt(ipAddress)) must_== Success(address)
  }

  def InetAddressToJString = {
    toJSON(address) must_== JString(host)
  }

  def JStringToInetSocketAddress = {
    fromJSON[InetSocketAddress](JString(withPort)) must_== Success(socketAddress)
  }

  def InetSocketAddressToJString = {
    toJSON(socketAddress) must_== JString(withPort)
  }

  def JStringToURL = {
    fromJSON[URL](JString(urlString)) must_== Success(url)
  }

  def URLToJString = {
    toJSON(url) must_== JString(urlString)
  }
}
