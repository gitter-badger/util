package org.sazabi.util.json
package formats

import org.json4s.{JInt, JString, JValue}
import org.json4s.scalaz.JsonScalaz._

import _root_.scala.collection.immutable.SortedSet

import _root_.scalaz._
import syntax.id._
import syntax.validation._

trait ScalaEnumFormats {
  /**
   * Enumeration with identified by id.
   */
  trait EnumerateById { self: Enumeration =>
    lazy val expected: SortedSet[Int] = values map(_.id)

    /**
     * Implicit instance of org.json4s.scalaz.JsonScalaz.JSONR.
     */
    implicit val valueJSONR: JSONR[Value] = Result2JSONR {
      case JInt(num) => {
        Validation.fromTryCatch(apply(num.intValue)).swap.map(unexpected).swap
      }
      case JString(str) => {
        Validation.fromTryCatch(apply(str.toInt)).swap.map(unexpected).swap
      }
      case j => UnexpectedJSONError(j, classOf[JInt]).failureNel
    }

    /**
     * Implicit instance of org.json4s.scalaz.JsonScalaz.JSONW.
     */
    implicit val valueJSONW: JSONW[Value] = new JSONW[Value] {
      def write(value: Value): JValue = JInt(value.id)
    }

    private[this] def unexpected(x: Throwable) = UncategorizedError(
      "Unexpected value", "Expected " + expected.mkString(" or "), Nil).wrapNel
  }

  /**
   * Enumeration with identified by name.
   */
  trait EnumerateByName { self: Enumeration =>
    lazy val expected: SortedSet[String] =
      SortedSet.empty[String] ++ values.map(_.toString)

    /**
     * Implicit instance of org.json4s.scalaz.JsonScalaz.JSONR.
     */
    implicit val valueJSONR: JSONR[Value] = Result2JSONR {
      case JInt(num) => 
        Validation.fromTryCatch(withName(num.toString)).swap.map(unexpected).swap
      case JString(str) =>
        Validation.fromTryCatch(withName(str)).swap.map(unexpected).swap
      case j => UnexpectedJSONError(j, classOf[JString]).failureNel
    }

    /**
     * Implicit instance of org.json4s.scalaz.JsonScalaz.JSONR.
     */
    implicit val valueJSONW: JSONW[Value] = new JSONW[Value] {
      def write(value: Value): JValue = JString(value.toString)
    }

    private[this] def unexpected(x: Throwable) = UncategorizedError(
      "Unexpected value", "Expected " + expected.mkString(" or "), Nil).wrapNel
  }
}

object scalaEnum extends ScalaEnumFormats
