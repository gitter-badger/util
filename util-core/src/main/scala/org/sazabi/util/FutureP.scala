package org.sazabi.util

import com.twitter.util.{Future, Return, Throw, Try}

import scalaz._

/**
 * A pimped trait for Future[A].
 */
trait FutureP[A] extends Pimped[Future[A]] {
  /**
   * Flattens Future[Try[B]] to Furure[B].
   */
  def flattenTry[B](implicit ev: A <:< Try[B]): Future[B] =
    value flatMap (Future.const(_))

  /**
   * Waits for the both futures and joins those.
   * Future#join() returns as soon as one of the futures returns Throw.
   */
  def waitJoin[B](f: Future[B]): Future[(A, B)] = {
    val f1 = Future(value.apply())
    val f2 = Future(f.apply())
    f1 join f2
  }
}

trait Futures {
  implicit def toFutureP[A](v: Future[A]): FutureP[A] = new FutureP[A] {
    val value = v
  }

  implicit val FutureInstance: Monad[Future] = new Monad[Future] {
    def point[A](a: => A): Future[A] = Future(a)
    def bind[A, B](fa: Future[A])(f: A => Future[B]): Future[B] = fa flatMap f
  }
}
