package org.sazabi.util.finagle.http.filter

import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.finagle.http.{Request, Response, Status, Version}
import com.twitter.finagle.core.util.InetAddressUtil
import com.twitter.util.{Future, NetUtil}

import java.net.InetAddress

import scalaz._
import syntax.std.option._

/**
 * A trait of filter that filters requests if those remote addresses aren't
 * from allowed ip blocks.
 * This trait uses X-Forwarded-For header if it exists.
 */
trait IpAddressFilter[Req <: Request, Res <: Response]
    extends SimpleFilter[Req, Res] {
  def allowedIpBlocks: Seq[(Int, Int)]
  def forbidden: Future[Res]

  protected def remoteAddress(request: Req): InetAddress = {
    val xForwardedFor = request.xForwardedFor map(InetAddressUtil.getByName(_))
    xForwardedFor | request.remoteAddress
  }

  def isAllowed(a: Int): Boolean = NetUtil.isIpInBlocks(a, allowedIpBlocks)

  /**
   * Filters a request.
   */
  def apply(request: Req, service: Service[Req, Res]): Future[Res] = {
    if (isAllowed(NetUtil.inetAddressToInt(remoteAddress(request)))) {
      service(request)
    } else {
      forbidden
    }
  }
}

/**
 * The companion object for IpAddressFilter.
 */
object IpAddressFilter {
  /**
   * Generates a IPAddressFilter that allows specified ip blocks.
   */
  def allow[Req <: Request, Res <: Response](blocks: Seq[(Int, Int)],
      fbdn: => Future[Res]): IpAddressFilter[Req, Res] =
    new IpAddressFilter[Req, Res] {
      val allowedIpBlocks = blocks
      def forbidden = fbdn
    }

  /**
   * Generates a IPAddressFilter that allows local or specified ip blocks.
   */
  def allowLocal[Req <: Request, Res <: Response](blocks: (Int, Int)*)(
    fbdn: => Future[Res]): IpAddressFilter[Req, Res] =
      new IpAddressFilter[Req, Res] {
        val allowedIpBlocks = localIpBlocks ++ blocks.toSeq
        def forbidden = fbdn
      }

  /**
   * Ip address blocks for local.
   */
  val localIpBlocks = {
    Seq("127.0.0.0/8", "10.0.0.0/8", "172.16.0.0/12", "192.168.0.0/16") map (
      NetUtil.cidrToIpBlock)
  }
}
