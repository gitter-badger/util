package org.sazabi.util.finagle.http

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.http.path.Path
import com.twitter.finagle.http.service.RoutingService

import org.jboss.netty.handler.codec.http.HttpMethod

import org.sazabi.util.-->

import scalaz._
import syntax.std.option._

/**
 * Router service builder.
 */
object Router {
  type RoutesByRequest[R <: Request] =
    (HttpMethod, Path) --> Service[R, Response]

  /**
   * Routes to a service by a method/path pair.
   */
  def by[R <: Request](routes: RoutesByRequest[R],
      opf: Option[String --> String] = None): RoutingService[R] =
    new RoutingService[R](
      new PartialFunction[Request, Service[R, Response]] {
        def apply(request: Request) =
          routes((request.method, path(request.path)))

        def isDefinedAt(request: Request) =
          routes.isDefinedAt((request.method, path(request.path)))

        private[this] def path(path: String): Path = Path(
          opf.filter(_ isDefinedAt path).map(_(path)) | path)
      }
    )
}
