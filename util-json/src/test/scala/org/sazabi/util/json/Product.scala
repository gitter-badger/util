package org.sazabi.util.json

import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.scalaz.JsonScalaz.{fromJSON, toJSON, JSON}

import org.scalatest._

import _root_.scalaz._

case class Person(name: String, age: Int)

object Person extends ((String, Int) => Person) with Protocol {
  implicit val JSONInstance =
    asProduct2("name", "age")(Person)(Person.unapply(_).get)
}

class ProductSpec extends FlatSpec with Matchers {

  val person = Person("hoge", 20)
  val json = """{"name":"hoge","age":20}"""

  "asProductN" should "lift case classes to JSON instance" in {
    val result = fromJSON(parse(json))(Person.JSONInstance)
    result should be (Success(person))

    result foreach { p =>
      val j = toJSON(p)
      j should be (JObject(List("name" -> JString("hoge"),
        "age" -> JInt(20))))

      val str = compact(render(j))
      str should be (json)
    }
  }
}
