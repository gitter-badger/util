package org.sazabi.util

import org.json4s.{JNothing, JObject, JValue}
import org.json4s.native.JsonMethods.{compact, render}

import _root_.scalaz._
import std.option._
import optionSyntax._
import std.string._
import syntax.equal._
import syntax.id._
import syntax.validation._

package object json {
  type Result[+A] = Validation[NonEmptyList[String], A]

  trait Reads[A] {
    def read(j: JValue): Result[A]
  }

  trait Writes[A] {
    def write(a: A): Result[JValue]
  }

  trait Formats[A] extends Reads[A] with Writes[A]

  def toJson[A: Writes](a: A): JValue =
    implicitly[Writes[A]].write(a).fold(nel => JNothing, identity)

  def fromJson[A: Reads](j: JValue): Result[A] =
    implicitly[Reads[A]].read(j)

  def field[A: Reads](name: String)(j: JValue): Result[A] = j match {
    case JObject(fields) => {
      fields.find(_._1 === name)
        .map(tuple => implicitly[Reads[A]].read(tuple._2))
        .orElse(implicitly[Reads[A]].read(JNothing)
          .fold(_ => none, r => r.success.some))
        .getOrElse("field '%s' is not found in %s".format(name, j).wrapNel.failure)
    }
    case _ => "Expected json object but %s".format(j).wrapNel.failure
  }

  implicit val jValueInstance: Monoid[JValue] with Show[JValue] =
      new Monoid[JValue] with Show[JValue] {
    def append(a: JValue, b: => JValue): JValue = a ++ b

    def zero: JValue = JNothing

    override def show(a: JValue): Cord =
      a.toOption.fold(j => Cord(compact(render(j))), Cord())
  }
}
