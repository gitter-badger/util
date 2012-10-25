package org.sazabi.util.querulous

import com.twitter.querulous.evaluator.{QueryEvaluator, Transaction}
import com.twitter.util.Future

import org.sazabi.util.Pimped

import scalaz._

import scalendar._

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

  def retrying[A](f: Transaction => A): Future[A] = AsyncDB.retrying(f)(value)

  def retrying[A](level: DB.IsolationLevel)(f: Transaction => A): Future[A] =
    AsyncDB.retrying(level)(f)(value)
}
