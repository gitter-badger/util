package org.sazabi.util.db

import Imports._

import org.sazabi.util.{AsyncPool, CachedAsyncPool, Pimped}

import com.twitter.ostrich.stats.Stats
import com.twitter.querulous.evaluator.{QueryEvaluator, Transaction}
import com.twitter.util.{Future, Return, Throw, Time, TimeFormat, Try}

import java.sql.SQLTransactionRollbackException

import scala.annotation.tailrec

import scalaz._

import scalendar._

object AsyncDB extends DB {
  protected val asyncPool: AsyncPool = new CachedAsyncPool {
    val threadFactoryName = "asyncdb"
  }
}

object DB {
  type IsolationLevel = IsolationLevel.Value

  object IsolationLevel extends Enumeration {
    val ReadCommitted = Value("READ COMMITTED")
    val RepeatableRead = Value("REPEATABLE READ")
    val Default = RepeatableRead
  }

  def read[A: RowReads](rs: java.sql.ResultSet): A =
    implicitly[RowReads[A]].reads(rs)
}

trait DB {
  import DB._

  protected def asyncPool: AsyncPool

  def transaction[A](f: Transaction => Future[A])(implicit qe: QueryEvaluator):
    Future[A] = asyncPool(qe.transaction(f(_)()))

  def retrying[A](f: Transaction => Future[A])(implicit qe: QueryEvaluator):
    Future[A] = trying(f)(qe)

  /** Run transaction with specified isolation level. */
  def transaction[A](level: IsolationLevel)(f: Transaction => Future[A])
    (implicit qe: QueryEvaluator): Future[A] = withIsolationLevel(level)(f)(qe)

  def retrying[A](level: IsolationLevel)(f: Transaction => Future[A])
    (implicit qe: QueryEvaluator): Future[A] = trying(level)(f)(qe)

  private def withIsolationLevel[A](level: IsolationLevel)
      (f: Transaction => Future[A])(implicit qe: QueryEvaluator): Future[A] = {
    asyncPool {
      qe.execute("SET SESSION TRANSACTION ISOLATION LEVEL " + level.toString)
      qe.transaction(f(_)())
    } ensure {
      qe.execute("SET SESSION TRANSACTION ISOLATION LEVEL " +
        IsolationLevel.Default.toString)
    }
  }

  private def trying[A](f: Transaction => Future[A])(qe: QueryEvaluator):
      Future[A] = {
    asyncPool(qe.transaction(f(_)())) rescue {
      case e: SQLTransactionRollbackException => {
        Stats.incr("transaction-deadlock")
        trying(f)(qe)
      }
    }
  }

  private def trying[A](level: IsolationLevel)(f: Transaction => Future[A])
      (implicit qe: QueryEvaluator): Future[A] = {
    withIsolationLevel(level)(f)(qe) rescue {
      case e: SQLTransactionRollbackException => {
        Stats.incr("transaction-deadlock")
        trying(level)(f)(qe)
      }
    }
  }
}

/**
 * A type class trait for deserialization from java.sql.ResultSet.
 */
trait RowReads[A] {
  def reads(rs: java.sql.ResultSet): A
}

/**
 * The Pimped trait for java.sql.Timestamp.
 */
trait TimestampP extends Pimped[java.sql.Timestamp] {
  def toScalendar: Scalendar = Scalendar(value.getTime())
}

trait Timestamps {
  implicit def toTimestampP(v: java.sql.Timestamp) = new TimestampP {
    val value = v
  }
}

/**
 * The pimped trait for java.sql.Date.
 */
trait SqlDateP extends Pimped[java.sql.Date] {
  def toScalendar: Scalendar = Scalendar(value.getTime())
}

trait SqlDates {
  implicit def toSqlDateP(v: java.sql.Date) = new SqlDateP {
    val value = v
  }
}

trait QueryEvaluators {
  /**
   * An implicit conversion for QueryEvaluatorP.
   */
  implicit def toQueryEvaluatorP(v: QueryEvaluator) = new QueryEvaluatorP {
    val value = v
  }
}

trait QueryEvaluatorP extends Pimped[QueryEvaluator] {
  def exist(query: String, params: Any*): Boolean =
    value.selectOne(query, params: _*)(_ => ()) isDefined

  def retrying[A](f: Transaction => Future[A]): Future[A] = AsyncDB.retrying(f)(value)

  def retrying[A](level: DB.IsolationLevel)(f: Transaction => Future[A]): Future[A] =
    AsyncDB.retrying(level)(f)(value)
}
