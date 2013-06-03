package org.sazabi.util.twitter

import com.twitter.util.{Future, Return, Throw, Try}

import scala.concurrent.{Future => SFuture, promise}
import scala.language.implicitConversions

import scalaz._

class FutureOps[A](val value: Future[A]) extends AnyVal {
  /**
   * Flattens Future[Try[B]] to Furure[B].
   */
  def flattenTry[B](implicit ev: A <:< Try[B]): Future[B] =
    value flatMap (Future.const(_))
}

trait ToFutureOps {
  implicit def toFutureOps[A]: Future[A] => FutureOps[A] = new FutureOps(_)
}

trait FutureTypeClasses {
  implicit val scalazFutureInstance: Monad[Future] = new Monad[Future] {
    def point[A](a: => A): Future[A] = Future(a)
    def bind[A, B](fa: Future[A])(f: A => Future[B]): Future[B] = fa flatMap f
  }
}

trait FutureToScala extends TryToScala {
  implicit def futureToScala[A](f: Future[A]): SFuture[A] = {
    val p = promise[A]

    f.respond { t: Try[A] =>
      p.complete(t)
    }

    p.future
  }
}
