package org.sazabi.util

import java.text.SimpleDateFormat

import org.json4s.{JInt, JValue}
import org.json4s.scalaz.JsonScalaz._

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

  /**
   * Implicit instance of scalaz.Order.
   */
  implicit val scalendarOrder: Order[Scalendar] = Order.orderBy(_.time)

  /**
   * Implicit instance of scalaz.Show.
   */
  implicit val scalendarShow: Show[Scalendar] =
    Show.show(cal => Cord(datetimeFormat.format(cal.time)))

  /**
   * Implicit instance of org.json4s.scalaz.JsonScalaz.JSONR.
   */
  implicit val scalendarJSONR: JSONR[Scalendar] = Result2JSONR {
    case JInt(num) => Scalendar(num.longValue).success
    case j => UnexpectedJSONError(j, classOf[JInt]).failureNel
  }

  /**
   * Implicit instance of org.json4s.scalaz.JsonScalaz.JSONW.
   */
  implicit val scalendarJSONW: JSONW[Scalendar] = new JSONW[Scalendar] {
    def write(value: Scalendar): JValue = JInt(value.time)
  }
}
