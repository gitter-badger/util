package org.sazabi.util

import com.twitter.util.{Future, Return, Throw, Try}

import scalaz._

/**
 * A pimped trait for Try[A].
 */
trait TryP[A] extends Pimped[Try[A]] {
  /**
   * Converts to Future[A].
   */
  def toFuture: Future[A] = Future.const(value)

  /**
   * Flattens Try[Future[B]] to Future[B].
   */
  def flattenToFuture[B](implicit ev: A <:< Future[B]): Future[B] = value match {
    case Return(v) => v
    case Throw(e) => Future.rawException(e)
  }
}

trait Trys {
  implicit def toTryP[A](v: Try[A]): TryP[A] = new TryP[A] {
    val value = v
  }

  implicit val TryInstance: Monad[Try] = new Monad[Try] {
    def point[A](a: => A): Try[A] = Try(a)
    def bind[A, B](fa: Try[A])(f: A => Try[B]): Try[B] = fa flatMap f
  }
}
