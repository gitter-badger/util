package org.sazabi.util

import java.text.SimpleDateFormat

import org.json4s.{JInt, JValue}

import org.sazabi.util.json.{Formats, Result}

import scalaz._
import syntax.id._
import syntax.validation._

import scalendar._

trait ScalendarTypeClasses {
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

  implicit val scalendarOrderInstance: Order[Scalendar] = Order.orderBy(_.time)

  implicit val scalendarShowInstance: Show[Scalendar] =
    Show.show(cal => Cord(datetimeFormat.format(cal.time)))

  implicit val scalendarFormatsInstance: Formats[Scalendar] = {
    Formats {
      case JInt(num) => Scalendar(num.longValue).success
      case _ => "Expected a long value as milliseconds from epoch".failureNel
    } {
      case scal => JInt(scal.time).success
    }
  }
}
