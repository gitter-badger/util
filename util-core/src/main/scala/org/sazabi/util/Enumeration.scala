package org.sazabi.util

import com.twitter.util.{Return, Throw, Try}

import org.json4s.{JInt, JString, JValue}
import org.json4s.JsonDSL._

import org.sazabi.util.json.{Formats, Result}

import scala.collection.SortedSet

import scalaz._
import syntax.validation._

/**
 * Enumeration with identified by id.
 */
trait EnumerateById { self: Enumeration =>
  lazy val expected: SortedSet[Int] = values.ids

  /** sjson Format. */
  implicit lazy val valueFormats: Formats[Value] = new Formats[Value] {
    def read(json: JValue): Result[Value] = json match {
      case JInt(num) => try(apply(num.intValue).success) catch {
        case _ => ("Expected " + expected.mkString(" or ")).failureNel
      }
      case JString(v) => try(apply(v.toInt).success) catch {
        case _ => ("Expected " + expected.mkString(" or ")).failureNel
      }
      case _ => ("Expected number or string").failureNel
    }

    def write(value: Value): Result[JValue] = JInt(value.id).success
  }
}

/**
 * Enumeration with identified by name.
 */
trait EnumerateByName { self: Enumeration =>
  lazy val expected: SortedSet[String] =
    SortedSet.empty[String] ++ values.map(_.toString)

  /** sjson Format. */
  implicit lazy val valueFormats: Formats[Value] = new Formats[Value] {
    def read(json: JValue): Result[Value] = json match {
      case JInt(num) => try(withName(num.toString).success) catch {
        case _ => ("Expected " + expected.mkString(" or ")).failureNel
      }
      case JString(name) => try(withName(name).success) catch {
        case _ => ("Expected " + expected.mkString(" or ")).failureNel
      }
      case _ => ("Expected number or string").failureNel
    }

    def write(value: Value): Result[JValue] = JString(value.toString).success
  }
}
