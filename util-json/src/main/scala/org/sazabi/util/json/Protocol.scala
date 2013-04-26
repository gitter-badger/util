package org.sazabi.util.json

import org.json4s.{JObject, JValue}
import org.json4s.JsonDSL.{jobject2assoc, pair2jvalue, pair2Assoc}
import org.json4s.scalaz.JsonScalaz._

import _root_.scalaz._
import syntax.applicative._
import syntax.id._
import syntax.validation._

trait Protocol {
  def asProduct1[A, V : JSONR : JSONW](v: String)
      (apply: V => A)(unapply: A => V): JSON[A] = new JSON[A] {
    def read(js: JValue): Result[A] = js match {
      case j @ JObject(_) => {
        Apply[Result].apply(field[V](v)(j))(apply)
      }
      case j => UnexpectedJSONError(j, classOf[JObject]).failureNel
    }

    def write(a: A): JValue = {
      val p = unapply(a)
      pair2jvalue(v -> toJSON(p))
    }
  }

  def asProduct2[A, V1 : JSONR : JSONW, V2 : JSONR : JSONW](v1: String, v2: String)
      (apply: (V1, V2) => A)(unapply: A => Product2[V1, V2]): JSON[A] =
        new JSON[A] {
    def read(js: JValue): Result[A] = js match {
      case j @ JObject(_) => {
        Apply[Result].apply2(field[V1](v1)(j),
          field[V2](v2)(j))(apply)
      }
      case j => UnexpectedJSONError(j, classOf[JObject]).failureNel
    }

    def write(a: A): JValue = {
      val product = unapply(a)
      (v1 -> toJSON(product._1)) ~ (v2 -> toJSON(product._2))
    }
  }

  def asProduct3[A, V1 : JSONR : JSONW, V2 : JSONR : JSONW, V3 : JSONR : JSONW]
      (v1: String, v2: String, v3: String)
      (apply: (V1, V2, V3) => A)(unapply: A => Product3[V1, V2, V3]): JSON[A] =
        new JSON[A] {
    def read(js: JValue): Result[A] = js match {
      case j @ JObject(_) => {
        Apply[Result].apply3(field[V1](v1)(j),
          field[V2](v2)(j),
          field[V3](v3)(j))(apply)
      }
      case j => UnexpectedJSONError(j, classOf[JObject]).failureNel
    }

    def write(a: A): JValue = {
      val product = unapply(a)
      (v1 -> toJSON(product._1)) ~
        (v2 -> toJSON(product._2)) ~
        (v3 -> toJSON(product._3))
    }
  }

  def asProduct4[A, V1 : JSONR : JSONW, V2 : JSONR : JSONW, V3 : JSONR : JSONW,
    V4 : JSONR : JSONW]
      (v1: String, v2: String, v3: String, v4: String)
      (apply: (V1, V2, V3, V4) => A)
      (unapply: A => Product4[V1, V2, V3, V4]): JSON[A] = new JSON[A] {
    def read(js: JValue): Result[A] = js match {
      case j @ JObject(_) => {
        Apply[Result].apply4(field[V1](v1)(j),
          field[V2](v2)(j),
          field[V3](v3)(j),
          field[V4](v4)(j))(apply)
      }
      case j => UnexpectedJSONError(j, classOf[JObject]).failureNel
    }

    def write(a: A): JValue = {
      val product = unapply(a)
      (v1 -> toJSON(product._1)) ~
        (v2 -> toJSON(product._2)) ~
        (v3 -> toJSON(product._3)) ~
        (v4 -> toJSON(product._4))
    }
  }

  def asProduct5[A, V1 : JSONR : JSONW, V2 : JSONR : JSONW, V3 : JSONR : JSONW,
      V4 : JSONR : JSONW, V5 : JSONR : JSONW]
      (v1: String, v2: String, v3: String, v4: String, v5: String)
      (apply: (V1, V2, V3, V4, V5) => A)
      (unapply: A => Product5[V1, V2, V3, V4, V5]): JSON[A] = new JSON[A] {
    def read(js: JValue): Result[A] = js match {
      case j @ JObject(_) => {
        Apply[Result].apply5(field[V1](v1)(j),
          field[V2](v2)(j),
          field[V3](v3)(j),
          field[V4](v4)(j),
          field[V5](v5)(j))(apply)
      }
      case j => UnexpectedJSONError(j, classOf[JObject]).failureNel
    }

    def write(a: A): JValue = {
      val product = unapply(a)
      (v1 -> toJSON(product._1)) ~
        (v2 -> toJSON(product._2)) ~
        (v3 -> toJSON(product._3)) ~
        (v4 -> toJSON(product._4)) ~
        (v5 -> toJSON(product._5))
    }
  }

  def asProduct6[A, V1 : JSONR : JSONW, V2 : JSONR : JSONW, V3 : JSONR : JSONW,
      V4 : JSONR : JSONW, V5 : JSONR : JSONW, V6 : JSONR : JSONW]
      (v1: String, v2: String, v3: String, v4: String, v5: String, v6: String)
      (apply: (V1, V2, V3, V4, V5, V6) => A)
      (unapply: A => Product6[V1, V2, V3, V4, V5, V6]): JSON[A] = new JSON[A] {
    def read(js: JValue): Result[A] = js match {
      case j @ JObject(_) => {
        Apply[Result].apply6(field[V1](v1)(j),
          field[V2](v2)(j),
          field[V3](v3)(j),
          field[V4](v4)(j),
          field[V5](v5)(j),
          field[V6](v6)(j))(apply)
      }
      case j => UnexpectedJSONError(j, classOf[JObject]).failureNel
    }

    def write(a: A): JValue = {
      val product = unapply(a)
      (v1 -> toJSON(product._1)) ~
        (v2 -> toJSON(product._2)) ~
        (v3 -> toJSON(product._3)) ~
        (v4 -> toJSON(product._4)) ~
        (v5 -> toJSON(product._5)) ~
        (v6 -> toJSON(product._6))
    }
  }

  def asProduct7[A, V1 : JSONR : JSONW, V2 : JSONR : JSONW, V3 : JSONR : JSONW,
      V4 : JSONR : JSONW, V5 : JSONR : JSONW, V6 : JSONR : JSONW,
      V7 : JSONR : JSONW]
      (v1: String, v2: String, v3: String, v4: String, v5: String, v6: String,
        v7: String)
      (apply: (V1, V2, V3, V4, V5, V6, V7) => A)
      (unapply: A => Product7[V1, V2, V3, V4, V5, V6, V7]): JSON[A] = new JSON[A] {
    def read(js: JValue): Result[A] = js match {
      case j @ JObject(_) => {
        Apply[Result].apply7(field[V1](v1)(j),
          field[V2](v2)(j),
          field[V3](v3)(j),
          field[V4](v4)(j),
          field[V5](v5)(j),
          field[V6](v6)(j),
          field[V7](v7)(j))(apply)
      }
      case j => UnexpectedJSONError(j, classOf[JObject]).failureNel
    }

    def write(a: A): JValue = {
      val product = unapply(a)
      (v1 -> toJSON(product._1)) ~
        (v2 -> toJSON(product._2)) ~
        (v3 -> toJSON(product._3)) ~
        (v4 -> toJSON(product._4)) ~
        (v5 -> toJSON(product._5)) ~
        (v6 -> toJSON(product._6)) ~
        (v7 -> toJSON(product._7))
    }
  }

  def asProduct8[A, V1 : JSONR : JSONW, V2 : JSONR : JSONW, V3 : JSONR : JSONW,
      V4 : JSONR : JSONW, V5 : JSONR : JSONW, V6 : JSONR : JSONW,
      V7 : JSONR : JSONW, V8 : JSONR : JSONW]
      (v1: String, v2: String, v3: String, v4: String, v5: String, v6: String,
        v7: String, v8: String)
      (apply: (V1, V2, V3, V4, V5, V6, V7, V8) => A)
      (unapply: A => Product8[V1, V2, V3, V4, V5, V6, V7, V8]):
      JSON[A] = new JSON[A] {
    def read(js: JValue): Result[A] = js match {
      case j @ JObject(_) => {
        Apply[Result].apply8(field[V1](v1)(j),
          field[V2](v2)(j),
          field[V3](v3)(j),
          field[V4](v4)(j),
          field[V5](v5)(j),
          field[V6](v6)(j),
          field[V7](v7)(j),
          field[V8](v8)(j))(apply)
      }
      case j => UnexpectedJSONError(j, classOf[JObject]).failureNel
    }

    def write(a: A): JValue = {
      val product = unapply(a)
      (v1 -> toJSON(product._1)) ~
        (v2 -> toJSON(product._2)) ~
        (v3 -> toJSON(product._3)) ~
        (v4 -> toJSON(product._4)) ~
        (v5 -> toJSON(product._5)) ~
        (v6 -> toJSON(product._6)) ~
        (v7 -> toJSON(product._7)) ~
        (v8 -> toJSON(product._8))
    }
  }

  def asProduct9[A, V1 : JSONR : JSONW, V2 : JSONR : JSONW, V3 : JSONR : JSONW,
      V4 : JSONR : JSONW, V5 : JSONR : JSONW, V6 : JSONR : JSONW,
      V7 : JSONR : JSONW, V8 : JSONR : JSONW, V9 : JSONR : JSONW]
      (v1: String, v2: String, v3: String, v4: String, v5: String, v6: String,
        v7: String, v8: String, v9: String)
      (apply: (V1, V2, V3, V4, V5, V6, V7, V8, V9) => A)
      (unapply: A => Product9[V1, V2, V3, V4, V5, V6, V7, V8, V9]):
      JSON[A] = new JSON[A] {
    def read(js: JValue): Result[A] = js match {
      case j @ JObject(_) => {
        Apply[Result].apply9(field[V1](v1)(j),
          field[V2](v2)(j),
          field[V3](v3)(j),
          field[V4](v4)(j),
          field[V5](v5)(j),
          field[V6](v6)(j),
          field[V7](v7)(j),
          field[V8](v8)(j),
          field[V9](v9)(j))(apply)
      }
      case j => UnexpectedJSONError(j, classOf[JObject]).failureNel
    }

    def write(a: A): JValue = {
      val product = unapply(a)
      (v1 -> toJSON(product._1)) ~
        (v2 -> toJSON(product._2)) ~
        (v3 -> toJSON(product._3)) ~
        (v4 -> toJSON(product._4)) ~
        (v5 -> toJSON(product._5)) ~
        (v6 -> toJSON(product._6)) ~
        (v7 -> toJSON(product._7)) ~
        (v8 -> toJSON(product._8)) ~
        (v9 -> toJSON(product._9))
    }
  }
}
