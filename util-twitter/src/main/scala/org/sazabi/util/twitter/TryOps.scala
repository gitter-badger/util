package org.sazabi.util.twitter

import com.twitter.util.{Future, Return, Throw, Try}

import scala.language.implicitConversions
import scala.util.{Try => STry}

import scalaz._

/**
 * A pimped trait for Try[A].
 */
class TryOps[A](val value: Try[A]) extends AnyVal {
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

trait ToTryOps {
  implicit def toTryOps[A]: Try[A] => TryOps[A] = new TryOps(_)
}

trait TryTypeClasses {
  implicit val scalazTryInstance: Monad[Try] = new Monad[Try] {
    def point[A](a: => A): Try[A] = Try(a)
    def bind[A, B](fa: Try[A])(f: A => Try[B]): Try[B] = fa flatMap f
  }
}

trait TryToScala {
  implicit def tryToScala[A](t: Try[A]): STry[A] = STry(t.get)
}
