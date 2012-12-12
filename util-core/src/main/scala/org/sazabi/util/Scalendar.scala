package org.sazabi.util

import java.text.SimpleDateFormat

import org.json4s.{JInt, JValue}

import org.sazabi.util.json.{Formats, Result}

import scalaz._
import syntax.id._
import syntax.validation._

import scalendar._

trait Scalendars {
  /**
   * A thread-local SimpleDateFormat for date and time.
   */
  private val localDatetimeFormat = new java.lang.ThreadLocal[SimpleDateFormat] {
    protected override def initialValue() = Pattern("yyyy-MM-dd HH:mm:ss")
  }

  /**
   * Implicitly returns a thread-local SimpleDateFormat.
   */
  implicit def datetimeFormat: SimpleDateFormat = localDatetimeFormat.get

  /**
   * A thread-local SimpleDateFormat for date only.
   */
  private val localDateFormat = new java.lang.ThreadLocal[SimpleDateFormat] {
    protected override def initialValue() = Pattern("yyyy-MM-dd")
  }

  /**
   * Implicitly returns a thread-local SimpleDateFormat.
   */
  def dateFormat: SimpleDateFormat = localDateFormat.get

  // Type classes for scalaz.
  implicit val scalendarOrder: Order[Scalendar] = Order.orderBy(_.time)
  implicit val scalendarShow: Show[Scalendar] =
    Show.show(cal => Cord(datetimeFormat.format(cal.time)))

  implicit val scalendarFormats: Formats[Scalendar] = new Formats[Scalendar] {
    def read(json: JValue): Result[Scalendar] = json match {
      case JInt(num) => Scalendar(num.longValue).success
      case _ => "Expected a long value as milliseconds from epoch".failureNel
    }

    def write(a: Scalendar): Result[JValue] = JInt(a.time).success
  }
}
