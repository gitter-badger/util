package org.sazabi.util.scalendar

import java.text.SimpleDateFormat

import scala.language.implicitConversions

import _root_.scalaz._

import scalendar.Scalendar

trait ScalendarScalazTypeClasses {
  /**
   * Implicit instance of scalaz.Order.
   */
  implicit val scalendarOrder: Order[Scalendar] = Order.orderBy(_.time)

  /**
   * Implicit instance of scalaz.Show.
   */
  implicit def scalendarShow(implicit sdf: SimpleDateFormat): Show[Scalendar] =
    Show.show(cal => Cord(sdf.format(cal.time)))

}

object scalaz extends ScalendarScalazTypeClasses
