package org.sazabi.util.scalendar

import java.text.SimpleDateFormat

import scala.language.implicitConversions

import _root_.scalaz._

import scalendar.Scalendar

trait ScalendarScalazTypeClasses {
  case class ScalendarFormat(format: SimpleDateFormat)

  /**
   * Implicit instance of scalaz.Order.
   */
  implicit val scalendarOrder: Order[Scalendar] = Order.orderBy(_.time)

  /**
   * Implicit instance of scalaz.Show.
   */
  implicit def scalendarShow(implicit sf: ScalendarFormat): Show[Scalendar] =
    Show.show(cal => Cord(sf.format.format(cal.time)))
}

object scalaz extends ScalendarScalazTypeClasses
