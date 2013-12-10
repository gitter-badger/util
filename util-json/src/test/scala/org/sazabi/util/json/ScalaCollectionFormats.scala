package org.sazabi.util
package json

import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.scalaz.JsonScalaz.{fromJSON, toJSON, JSON}

import org.scalatest._

import scala.collection.immutable.BitSet

import _root_.scalaz._

class ScalaCollectionFormatsSpec extends FunSpec with Matchers
    with formats.ScalaCollectionFormats {
  describe("BitSet") {
    describe("JSONR") {
      it("should convert JInt to BitSet") {
        fromJSON[BitSet](JInt(10)) shouldBe
          Success(BitSet.fromBitMask(Array(10)))
      }

      it("should convert JArray(JInt...) to BitSet") {
        fromJSON[BitSet](JArray(List(JInt(1), JInt(3)))) shouldBe
          Success(BitSet.fromBitMask(Array(1, 3)))
      }

      it("should fail to convert JString/JObject to BitSet") {
        fromJSON[BitSet](JString("1")) shouldBe 'failure
        fromJSON[BitSet](JObject(List("bits" -> JInt(1)))) shouldBe 'failure
      }
    }

    describe("JSONW") {
      it("should convert BitSet to JInt") {
        toJSON(BitSet.fromBitMask(Array(12))) shouldBe JInt(12)
        toJSON(BitSet.fromBitMask(Array(1, 3))) shouldBe
          JInt(BigInt("55340232221128654849"))
      }
    }
  }
}
