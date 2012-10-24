package org.sazabi.util.finagle.http.filter

import com.twitter.finagle.{Service, Filter}
import com.twitter.finagle.http.{Request, Response, Version, Status}
import com.twitter.util.Future

import org.apache.commons.codec.binary.Base64

import scalaz._

/**
 * A trait for http basic authorization.
 */
trait BasicAuthorizationFilter[A <: Request, B <: Request with BasicAuthorized]
    extends Filter[A, Response, B, Response] {
  def realm: String

  def authorize(username: String, password: String): Boolean

  def createRequest(request: A, realm: String, username: String): B

  def apply(request: A, service: Service[B, Response]): Future[Response] = {
    request.authorization flatMap { auth =>
      auth.split(' ').drop(1).headOption flatMap { part =>
        new String(Base64.decodeBase64(part.getBytes)).split(':').toSeq match {
          case Seq(u, p) if authorize(u, p) => Some(u)
          case _ => None
        }
      }
    } match {
      case Some(username) => service(createRequest(request, realm, username))
      case None => {
        val res = Response(Version.Http11, Status.Unauthorized)
        res.wwwAuthenticate = """Basic realm="%s"""".format(realm)
        Future.value(res)
      }
    }
  }
}

/**
 * A trait for http basic authorized request.
 */
trait BasicAuthorized { self: Request =>
  def realm: String
  def username: String
}
