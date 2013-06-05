package org.sazabi.util.json
package formats

import com.twitter.finagle.core.util.InetAddressUtil
import com.twitter.finagle.util.InetSocketAddressUtil
import com.twitter.util.NetUtil

import java.net.{InetAddress, InetSocketAddress, URL}

import org.json4s.{JInt, JString}
import org.json4s.scalaz.JsonScalaz._

import _root_.scalaz._
import syntax.id._
import syntax.validation._

trait JavaNetFormats {
  /**
   * Implicit JSONR instance for InetAddress.
   */
  implicit val InetAddressJSONR: JSONR[InetAddress] = Result2JSONR {
    case JString(host) => InetAddressUtil.getByName(host).success
    case JInt(n) => {
      val i = n.intValue
      val bytes = Array[Byte](
        ((n & 0xff000000) >> 24).toByte,
        ((n & 0x00ff0000) >> 16).toByte,
        ((n & 0x0000ff00) >>  8).toByte,
        ((n & 0x000000ff)).toByte)

        InetAddress.getByAddress(bytes).success
    }
    case j => UnexpectedJSONError(j, classOf[JString]).failNel
  }

  /**
   * Implicit JSONW instance of InetAddress.
   */
  implicit val InetAddressJSONW: JSONW[InetAddress] = toJSONW { addr =>
    val str = addr.toString
    JString(str.drop(str.indexOf('/') + 1))
  }

  /**
   * Implicit JSONR instance of InetSocketAddress.
   */
  implicit val InetSocketAddressJSONR: JSONR[InetSocketAddress] =
    Result2JSONR {
      case JString(host) => {
        Validation.fromTryCatch(InetSocketAddressUtil.parseHosts(host).head)
          .leftMap { e => 
            UncategorizedError("Invalid format for InetSocketAddress",
              e.getMessage, Nil).wrapNel
          }
      }
      case j => UnexpectedJSONError(j, classOf[JString]).failNel
    }

  /**
   * Implicit JSONW instance of InetSocketAddress.
   */
  implicit val InetSocketAddressJSONW: JSONW[InetSocketAddress] = toJSONW {
    addr =>
      val str = addr.toString
      JString(str.drop(str.indexOf('/') + 1))
  }

  /**
   * Implicit JSONR instance of URL.
   */
  implicit val URLJSONR: JSONR[URL] = Result2JSONR {
    case JString(url) => Validation.fromTryCatch(new URL(url))
      .leftMap { e =>
        UncategorizedError("Malformed URL", e.getMessage, Nil)
          .wrapNel
      }
    case j => UnexpectedJSONError(j, classOf[JString]).failNel
  }

  /**
   * Implicit JSONW instance of URL.
   */
  implicit val URLJSONW: JSONW[URL] = toJSONW { url =>
    JString(url.toString)
  }
}

object javaNet extends JavaNetFormats
