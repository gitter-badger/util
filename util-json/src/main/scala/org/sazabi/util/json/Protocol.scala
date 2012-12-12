package org.sazabi.util.json

import org.json4s.{JObject, JValue}
import org.json4s.JsonDSL.{jobject2assoc, pair2Assoc}

import scalaz._
import syntax.applicative._
import syntax.id._
import syntax.validation._

trait Protocol {
  def asProduct2[A, V1, V2](v1: String, v2: String)
      (apply: (V1, V2) => A)(unapply: A => Product2[V1, V2])
      (implicit v1f: Formats[V1], v2f: Formats[V2]): Formats[A] = new Formats[A] {
    def read(js: JValue): Result[A] = js match {
      case j @ JObject(_) => {
        Apply[Result].apply2(field[V1](v1)(j),
          field[V2](v2)(j))(apply)
      }
      case _ => "Object expected".wrapNel.failure
    }

    def write(a: A): Result[JValue] = {
      val product = unapply(a)
      ((v1 -> toJson(product._1)) ~
        (v2 -> toJson(product._2))).success
    }
  }

  def asProduct3[A, V1, V2, V3](v1: String, v2: String, v3: String)
      (apply: (V1, V2, V3) => A)(unapply: A => Product3[V1, V2, V3])
      (implicit v1f: Formats[V1], v2f: Formats[V2], v3f: Formats[V3]):
        Formats[A] = new Formats[A] {
    def read(js: JValue): Result[A] = js match {
      case j @ JObject(_) => {
        Apply[Result].apply3(field[V1](v1)(j),
          field[V2](v2)(j),
          field[V3](v3)(j))(apply)
      }
      case _ => "Object expected".wrapNel.failure
    }

    def write(a: A): Result[JValue] = {
      val product = unapply(a)
      Success((v1 -> toJson(product._1)) ~
        (v2 -> toJson(product._2)) ~
        (v3 -> toJson(product._3)))
    }
  }

  def asProduct4[A, V1, V2, V3, V4]
      (v1: String, v2: String, v3: String, v4: String)
      (apply: (V1, V2, V3, V4) => A)(unapply: A => Product4[V1, V2, V3, V4])
      (implicit v1f: Formats[V1], v2f: Formats[V2], v3f: Formats[V3],
        v4f: Formats[V4]): Formats[A] = new Formats[A] {
    def read(js: JValue): Result[A] = js match {
      case j @ JObject(_) => {
        Apply[Result].apply4(field[V1](v1)(j),
          field[V2](v2)(j),
          field[V3](v3)(j),
          field[V4](v4)(j))(apply)
      }
      case _ => "Object expected".wrapNel.failure
    }

    def write(a: A): Result[JValue] = {
      val product = unapply(a)
      Success((v1 -> toJson(product._1)) ~
        (v2 -> toJson(product._2)) ~
        (v3 -> toJson(product._3)) ~
        (v4 -> toJson(product._4)))
    }
  }

  def asProduct5[A, V1, V2, V3, V4, V5]
      (v1: String, v2: String, v3: String, v4: String, v5: String)
      (apply: (V1, V2, V3, V4, V5) => A)(unapply: A => Product5[V1, V2, V3, V4, V5])
      (implicit v1f: Formats[V1], v2f: Formats[V2], v3f: Formats[V3],
        v4f: Formats[V4], vf5: Formats[V5]): Formats[A] = new Formats[A] {
    def read(js: JValue): Result[A] = js match {
      case j @ JObject(_) => {
        Apply[Result].apply5(field[V1](v1)(j),
          field[V2](v2)(j),
          field[V3](v3)(j),
          field[V4](v4)(j),
          field[V5](v5)(j))(apply)
      }
      case _ => "Object expected".wrapNel.failure
    }

    def write(a: A): Result[JValue] = {
      val product = unapply(a)
      Success((v1 -> toJson(product._1)) ~
        (v2 -> toJson(product._2)) ~
        (v3 -> toJson(product._3)) ~
        (v4 -> toJson(product._4)) ~
        (v5 -> toJson(product._5)))
    }
  }

  def asProduct6[A, V1, V2, V3, V4, V5, V6]
      (v1: String, v2: String, v3: String, v4: String, v5: String, v6: String)
      (apply: (V1, V2, V3, V4, V5, V6) => A)
      (unapply: A => Product6[V1, V2, V3, V4, V5, V6])
      (implicit v1f: Formats[V1], v2f: Formats[V2], v3f: Formats[V3],
        v4f: Formats[V4], v5f: Formats[V5], v6f: Formats[V6]):
      Formats[A] = new Formats[A] {
    def read(js: JValue): Result[A] = js match {
      case j @ JObject(_) => {
        Apply[Result].apply6(field[V1](v1)(j),
          field[V2](v2)(j),
          field[V3](v3)(j),
          field[V4](v4)(j),
          field[V5](v5)(j),
          field[V6](v6)(j))(apply)
      }
      case _ => "Object expected".wrapNel.failure
    }

    def write(a: A): Result[JValue] = {
      val product = unapply(a)
      Success((v1 -> toJson(product._1)) ~
        (v2 -> toJson(product._2)) ~
        (v3 -> toJson(product._3)) ~
        (v4 -> toJson(product._4)) ~
        (v5 -> toJson(product._5)) ~
        (v6 -> toJson(product._6)))
    }
  }

  def asProduct7[A, V1, V2, V3, V4, V5, V6, V7]
      (v1: String, v2: String, v3: String, v4: String, v5: String, v6: String,
        v7: String)
      (apply: (V1, V2, V3, V4, V5, V6, V7) => A)
      (unapply: A => Product7[V1, V2, V3, V4, V5, V6, V7])
      (implicit v1f: Formats[V1], v2f: Formats[V2], v3f: Formats[V3],
        v4f: Formats[V4], v5f: Formats[V5], v6f: Formats[V6], v7f: Formats[V7]):
      Formats[A] = new Formats[A] {
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
      case _ => "Object expected".wrapNel.failure
    }

    def write(a: A): Result[JValue] = {
      val product = unapply(a)
      Success((v1 -> toJson(product._1)) ~
        (v2 -> toJson(product._2)) ~
        (v3 -> toJson(product._3)) ~
        (v4 -> toJson(product._4)) ~
        (v5 -> toJson(product._5)) ~
        (v6 -> toJson(product._6)) ~
        (v7 -> toJson(product._7)))
    }
  }

  def asProduct8[A, V1, V2, V3, V4, V5, V6, V7, V8]
      (v1: String, v2: String, v3: String, v4: String, v5: String, v6: String,
        v7: String, v8: String)
      (apply: (V1, V2, V3, V4, V5, V6, V7, V8) => A)
      (unapply: A => Product8[V1, V2, V3, V4, V5, V6, V7, V8])
      (implicit v1f: Formats[V1], v2f: Formats[V2], v3f: Formats[V3],
        v4f: Formats[V4], v5f: Formats[V5], v6f: Formats[V6], v7f: Formats[V7],
        v8f: Formats[V8]): Formats[A] = new Formats[A] {
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
      case _ => "Object expected".wrapNel.failure
    }

    def write(a: A): Result[JValue] = {
      val product = unapply(a)
      Success((v1 -> toJson(product._1)) ~
        (v2 -> toJson(product._2)) ~
        (v3 -> toJson(product._3)) ~
        (v4 -> toJson(product._4)) ~
        (v5 -> toJson(product._5)) ~
        (v6 -> toJson(product._6)) ~
        (v7 -> toJson(product._7)) ~
        (v8 -> toJson(product._8)))
    }
  }

  def asProduct9[A, V1, V2, V3, V4, V5, V6, V7, V8, V9]
      (v1: String, v2: String, v3: String, v4: String, v5: String, v6: String,
        v7: String, v8: String, v9: String)
      (apply: (V1, V2, V3, V4, V5, V6, V7, V8, V9) => A)
      (unapply: A => Product9[V1, V2, V3, V4, V5, V6, V7, V8, V9])
      (implicit v1f: Formats[V1], v2f: Formats[V2], v3f: Formats[V3],
        v4f: Formats[V4], v5f: Formats[V5], v6f: Formats[V6], v7f: Formats[V7],
        v8f: Formats[V8], v9f: Formats[V9]): Formats[A] = new Formats[A] {
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
      case _ => "Object expected".wrapNel.failure
    }

    def write(a: A): Result[JValue] = {
      val product = unapply(a)
      Success((v1 -> toJson(product._1)) ~
        (v2 -> toJson(product._2)) ~
        (v3 -> toJson(product._3)) ~
        (v4 -> toJson(product._4)) ~
        (v5 -> toJson(product._5)) ~
        (v6 -> toJson(product._6)) ~
        (v7 -> toJson(product._7)) ~
        (v8 -> toJson(product._8)) ~
        (v9 -> toJson(product._9)))
    }
  }
}
