package org.sazabi.util.finagle.http.filter

import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.finagle.http.{Request, Response, Version, Status}
import com.twitter.util.Future

import scalaz._

/**
 * The filter that limits content-length header field or
 * acturl content length if no header field exist.
 */
case class ContentLengthFilter[Req <: Request](min: Long = 0L, max: Long = Long.MaxValue)
    extends SimpleFilter[Req, Response] {
  def apply(req: Req, service: Service[Req, Response]): Future[Response] = {
    val len = req.content.readableBytes.toLong
    if (len < min) {
      val res = Response(Version.Http11, Status.BadRequest)
      res.contentString = "Invalid content length"
      Future.value(res)
    } else if (len > max) {
      val res = Response(Version.Http11, Status.RequestEntityTooLarge)
      res.contentString = "Content length is too large"
      Future.value(res)
    } else {
      service(req)
    }
  }
}
