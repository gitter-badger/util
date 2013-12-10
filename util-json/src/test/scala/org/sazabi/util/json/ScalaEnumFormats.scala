package org.sazabi.util.json

import org.json4s._
import org.json4s.scalaz.JsonScalaz.{fromJSON, toJSON, JSON}

import org.sazabi.util.json.formats.scalaEnum._

import org.scalatest._

import _root_.scalaz._

object ById extends Enumeration with EnumerateById {
  val one = Value(1)
  val two = Value(2)
  val three = Value(3)
}

object ByName extends Enumeration with EnumerateByName {
  val one = Value("one")
  val two = Value("two")
  val num = Value("1")
}

class ScalaEnumFormatsSpec extends FlatSpec with Matchers {
  "EnumerateById" should "have an implicit JSON[Value] for JInt" in {
    import ById.{one, two, three, Value}

    toJSON(one) shouldBe JInt(1)
    toJSON(two) shouldBe JInt(2)
    toJSON(three) shouldBe JInt(3)

    fromJSON[Value](JInt(1)) shouldBe Success(one)
    fromJSON[Value](JInt(2)) shouldBe Success(two)
    fromJSON[Value](JInt(3)) shouldBe Success(three)

    fromJSON[Value](JString("1")) shouldBe Success(one)

    fromJSON[Value](JInt(0)) should be a 'failure
    fromJSON[Value](JString("hoge")) should be a 'failure
    fromJSON[Value](JObject(List())) should be a 'failure
  }

  "EnumerateByName" should "have an implicit JSON[Value] for JString" in {
    import ByName.{one, two, num, Value}

    toJSON(one) shouldBe JString("one")
    toJSON(two) shouldBe JString("two")
    toJSON(num) shouldBe JString("1")

    fromJSON[Value](JString("one")) shouldBe Success(one)
    fromJSON[Value](JString("two")) shouldBe Success(two)
    fromJSON[Value](JString("1")) shouldBe Success(num)

    fromJSON[Value](JInt(1)) shouldBe Success(num)

    fromJSON[Value](JString("hoge")) should be a 'failure
    fromJSON[Value](JInt(0)) should be a 'failure
    fromJSON[Value](JObject(List())) should be a 'failure
  }
}
