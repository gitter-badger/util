package org.sazabi.util.querulous

import Imports._

import org.sazabi.util.{AsyncPool, CachedAsyncPool}

import com.twitter.ostrich.stats.Stats
import com.twitter.querulous.evaluator.{QueryEvaluator, Transaction}
import com.twitter.util.Future

import java.sql.SQLTransactionRollbackException

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

  def transaction[A](f: Transaction => A)(implicit qe: QueryEvaluator):
    Future[A] = asyncPool(qe.transaction(f))

  def retrying[A](f: Transaction => A)(implicit qe: QueryEvaluator):
    Future[A] = trying(f)(qe)

  /** Run transaction with specified isolation level. */
  def transaction[A](level: IsolationLevel)(f: Transaction => A)
    (implicit qe: QueryEvaluator): Future[A] = withIsolationLevel(level)(f)(qe)

  def retrying[A](level: IsolationLevel)(f: Transaction => A)
    (implicit qe: QueryEvaluator): Future[A] = trying(level)(f)(qe)

  private def withIsolationLevel[A](level: IsolationLevel)
      (f: Transaction => A)(implicit qe: QueryEvaluator): Future[A] = {
    asyncPool {
      qe.execute("SET SESSION TRANSACTION ISOLATION LEVEL " + level.toString)
      qe.transaction(f)
    } ensure {
      qe.execute("SET SESSION TRANSACTION ISOLATION LEVEL " +
        IsolationLevel.Default.toString)
    }
  }

  private def trying[A](f: Transaction => A)(qe: QueryEvaluator): Future[A] = {
    asyncPool(qe.transaction(f)) rescue {
      case e: SQLTransactionRollbackException => {
        Stats.incr("transaction-deadlock")
        trying(f)(qe)
      }
    }
  }

  private def trying[A](level: IsolationLevel)(f: Transaction => A)
      (qe: QueryEvaluator): Future[A] = {
    withIsolationLevel(level)(f)(qe) rescue {
      case e: SQLTransactionRollbackException => {
        Stats.incr("transaction-deadlock")
        trying(level)(f)(qe)
      }
    }
  }
}
