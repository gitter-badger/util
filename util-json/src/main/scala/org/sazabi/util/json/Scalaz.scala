package org.sazabi.util.json

import org.json4s.{JDecimal, JDouble, JInt, JNothing, JString, JValue}
import org.json4s.jackson.JsonMethods.{compact, render}

import _root_.scalaz._
import std.anyVal._
import std.math.bigDecimal._
import std.math.bigInt._
import std.string._
import syntax.monoid._

trait ScalazTypeClasses {
  implicit def jValueShow[A <: JValue]: Show[A] = Show.shows {
    case JNothing => ""
    case json => compact(render(json))
  }

  implicit def jIntMonoid: Monoid[JInt] = Monoid.instance(
    { case (JInt(n1), JInt(n2)) => JInt(n1 |+| n2) }, JInt(mzero[BigInt]))

  implicit def jDoubleMonoid: Monoid[JDouble] = Monoid.instance(
    { case (JDouble(d1), JDouble(d2)) => JDouble(d1 |+| d2) },
    JDouble(mzero[Double]))

  implicit def jDecimalMonoid: Monoid[JDecimal] = Monoid.instance(
    { case (JDecimal(d1), JDecimal(d2)) => JDecimal(d1 |+| d2) },
    JDecimal(mzero[BigDecimal]))

  implicit def jStringMonoid: Monoid[JString] = Monoid.instance(
    { case (JString(s1), JString(s2)) => JString(s1 |+| s2) },
    JString(mzero[String]))
}

object scalaz extends ScalazTypeClasses
