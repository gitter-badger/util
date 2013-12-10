package org.sazabi.util.json

import org.json4s._
import org.json4s.scalaz.JsonScalaz._

import org.sazabi.util.json.scalaz._

import org.scalatest._

import _root_.scalaz._
import syntax.monoid._

class ScalazInstanceSpec extends FunSpec with Matchers {
  val n1 = 10
  val n2 = 15
  val n3 = n1 + n2

  val s1 = "abc"
  val s2 = "xyz"
  val s3 = s1 + s2

  val v1: JValue = JString("test")
  val v2: JValue = JInt(10)

  describe("Scalaz instances") {
    describe("Monoid") {
      describe("Monoid[JValue]") {
        it("|+| should append values into JArray") {
          v1 |+| v2 should be (JArray(v1 :: v2 :: Nil))
        }

        it("x |+| mzero[JValue] should be x") {
          v1 |+| mzero[JValue] should be (v1)
        }
      }

      describe("Monoid[JInt]") {
        it("if x + y is z, JInt(x) |+| JInt(y) should be JInt(z)") {
          JInt(n1) |+| JInt(n2) should be (JInt(n3))
        }

        it("x |+| mzero[JInt] should be x") {
          JInt(n1) |+| mzero[JInt] should be (JInt(n1))
        }
      }

      describe("Monoid[JString]") {
        it("if str1 + str2 is str3, JString(str1) |+| JString(str3) " +
            "should be JString(str)") {
          JString(s1) |+| JString(s2) should be (JString(s3))
        }

        it("x |+| mzero[JString] should be x") {
          JString(s1) |+| mzero[JString] should be (JString(s1))
        }
      }
    }
  }
}
