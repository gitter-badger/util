package org.sazabi.util.finagle.http.filter

import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.finagle.http.{Request, Response, Status, Version}
import com.twitter.util.Future

case class MediaTypeFilter[Req <: Request](types: Seq[String])
    extends SimpleFilter[Req, Response] {
  def apply(req: Req, service: Service[Req, Response]): Future[Response] = {
    req.mediaType match {
      case Some(media) if types contains media => service(req)
      case _ =>
        Future.value(Response(Version.Http11, Status.UnsupportedMediaType))
    }
  }
}
