package org.sazabi.util.finagle.http.filter

import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.finagle.http.{Request, Response, Status, Version}
import com.twitter.util.{Future, NetUtil}

import java.net.InetAddress

/**
 * A trait of filter that filters requests
 * if those aren't from allowed ip blocks.
 */
trait IpAddressFilter[Req <: Request] extends SimpleFilter[Req, Response] {
  def allowedIpBlocks: Seq[(Int, Int)]

  protected def remoteAddress(request: Req): InetAddress = request.remoteAddress

  def isAllowed(a: Int): Boolean = NetUtil.isIpInBlocks(a, allowedIpBlocks)
 
  /**
   * Filters a request.
   */
  def apply(request: Req, service: Service[Req, Response]): Future[Response] = {
    if (isAllowed(NetUtil.inetAddressToInt(remoteAddress(request)))) {
      service(request)
    } else {
      Future.value(Response(Version.Http11, Status.Forbidden))
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
  def allow[Req <: Request](blocks: Seq[(Int, Int)]): IpAddressFilter[Req] =
      new IpAddressFilter[Req] {
    val allowedIpBlocks = blocks
  }

  /**
   * Generates a IPAddressFilter that allows local or specified ip blocks.
   */
  def allowLocal[Req <: Request](blocks: (Int, Int)*): IpAddressFilter[Req] =
    new IpAddressFilter[Req] {
      val allowedIpBlocks = localIpBlocks ++ blocks.toSeq
    }

  /**
   * Ip address blocks for local.
   */
  val localIpBlocks = {
    Seq("127.0.0.0/8", "10.0.0.0/8", "172.16.0.0/12", "192.168.0.0/16") map (
      NetUtil.cidrToIpBlock)
  }
}
