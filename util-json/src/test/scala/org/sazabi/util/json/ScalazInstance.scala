package org.sazabi.util.json

import org.sazabi.util.json.scalaz._

import org.json4s._
import org.json4s.scalaz.JsonScalaz._

import org.specs2._

import _root_.scalaz._
import syntax.monoid._

class ScalazInstanceSpec extends Specification {
  def is =
    "Monoid" ^ p^
      "Monoid[JValue] should" ^
        "|+| appends values into JArray" ! monoidJValueAppend ^
        "x |+| mzero[JValue] == x" ! monoidJValueZero ^ end ^
      "Monoid[JInt] should" ^
        "If int x + y == z, JInt(x) |+| JInt(y) == JInt(z)" ! monoidJIntAppend ^
        "x |+| mzero[JInt] == x"  ! monoidJIntZero ^ end ^
      "Monoid[JString] should" ^
        "If str1 + str2 == str3, JString(str1) |+| JString(str2) == JString(str3)" ! monoidJStringAppend ^
        "x |+| mzero[JString] == x" ! monoidJStringZero ^ end

  val n1 = 10
  val n2 = 15
  val n3 = n1 + n2

  val s1 = "abc"
  val s2 = "xyz"
  val s3 = s1 + s2

  val v1: JValue = JString("test")
  val v2: JValue = JInt(10)

  def monoidJValueAppend = {
    v1 |+| v2 must_== JArray(v1 :: v2 :: Nil)
  }

  def monoidJValueZero = {
    v1 |+| mzero[JValue] must_== v1
  }

  def monoidJIntAppend = {
    JInt(n1) |+| JInt(n2) must_== JInt(n3)
  }

  def monoidJIntZero = {
    JInt(n1) |+| mzero[JInt] must_== JInt(n1)
  }

  def monoidJStringAppend = {
    JString(s1) |+| JString(s2) must_== JString(s3)
  }

  def monoidJStringZero = {
    JString(s1) |+| mzero[JString] must_== JString(s1)
  }
}
