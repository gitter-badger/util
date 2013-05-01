package org.sazabi.util.scalendar

import com.twitter.bijection._

import java.text.SimpleDateFormat

import scala.language.implicitConversions

import scalendar.{fromString, Scalendar}

trait ScalendarBijections {
  implicit def ScalendarStringInjection(implicit sdf: SimpleDateFormat):
    Injection[Scalendar, String] = new AbstractInjection[Scalendar, String] {
      def apply(cal: Scalendar): String = sdf.format(cal)

      override def invert(str: String): Option[Scalendar] =
        try(Some(fromString(str))) catch {
          case x: Throwable => None
        }
    }

  implicit val ScalendarLongBijection: Bijection[Scalendar, Long] =
    new AbstractBijection[Scalendar, Long] {
      def apply(cal: Scalendar): Long = cal.time

      override def invert(millis: Long): Scalendar = Scalendar(millis)
    }
}

object bijections extends ScalendarBijections
