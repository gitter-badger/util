package org.sazabi.util.finagle.http

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.http.path.Path
import com.twitter.finagle.http.service.RoutingService

import org.jboss.netty.handler.codec.http.HttpMethod

import scalaz._

/**
 * Router service builder.
 */
object Router {
  private type RoutesByObject[A] =
    PartialFunction[(HttpMethod, Path), Service[A, Response]]

  /**
   * Routes to a service by a method/path pair.
   */
  def by[A](routes: RoutesByObject[A]) = new RoutingService(
    new PartialFunction[Request, Service[A, Response]] {
      def apply(request: Request) =
        routes((request.method, Path(request.path)))

      def isDefinedAt(request: Request) =
        routes.isDefinedAt((request.method, Path(request.path)))
    }
  )
}
