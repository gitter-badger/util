package org.sazabi.util.argonaut
package codecs

import argonaut._
import argonaut.Json._
import argonaut.JsonIdentity._

import org.joda.time.{ DateTime, LocalDate }

import org.scalatest._

import _root_.scalaz._

class JodaTimeCodecSpec extends FunSpec with Matchers with JodaTimeCodecs {
  describe("DateTime") {
    describe("DecodeJson") {
      it("should decode jNumber to DateTime") {
        def test(v: Long) {
          jNumber(v).jdecode[DateTime].value shouldBe Some(new DateTime(v))
        }

        test(System.currentTimeMillis)
        test(Long.MinValue)
        test(Long.MaxValue)
      }

      it("should decode jString to DateTime") {
        def test(v: String) {
          jString(v).jdecode[DateTime].value shouldBe Some(
            new DateTime(v.toLong))
        }

        test(System.currentTimeMillis.toString)
        test(Long.MaxValue.toString)

        jString("booo").jdecode[DateTime].value shouldBe empty
      }
    }

    describe("EncodeJson") {
      it("should encode DateTime to jString") {
        def test(v: Long) {
          val dt = new DateTime(v)
          dt.asJson shouldBe jString(v.toString)
        }

        test(System.currentTimeMillis)
        test(Long.MinValue)
        test(Long.MaxValue)
      }
    }
  }
}
