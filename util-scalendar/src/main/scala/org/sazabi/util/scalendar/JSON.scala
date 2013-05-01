package org.sazabi.util.scalendar

import org.json4s.{JInt, JValue}
import org.json4s.scalaz.JsonScalaz._

import _root_.scalaz._
import syntax.validation._

import scalendar.Scalendar

trait ScalendarJSONTypeClasses {
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

object json extends ScalendarJSONTypeClasses
