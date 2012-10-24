package org.sazabi.util.json

import net.liftweb.json._

import scalaz._
import std.option._
import optionSyntax._
import std.string._
import syntax.equal._
import syntax.id._
import syntax.validation._
import syntax.std.list._

object DefaultFormats {
  implicit val booleanFormats: Formats[Boolean] = new Formats[Boolean] {
    def read(j: JValue): Result[Boolean] = j match {
      case JBool(b) => Success(b)
      case JInt(n) => Success(n.longValue >= 0L)
      case JDouble(d) => Success(d > 0d)
      case JString(s) if s === "true" || s === "1" => Success(true)
      case JString(s) if s === "false" || s === "0"  => Success(false)
      case _ => "Expected a json boolean but %s".format(j.toString).wrapNel.failure
    }

    def write(b: Boolean): Result[JValue] = Success(JBool(b))
  }

  implicit val intFormats: Formats[Int] = new Formats[Int] {
    def read(j: JValue): Result[Int] = j match {
      case JString(str) => try (Success(str.toInt)) catch {
        case e => "Expected a number value but %s".format(j.toString).wrapNel.failure
      }
      case JInt(num) => Success(num.intValue)
      case _ => "Expected a json number but %s".format(j.toString).wrapNel.failure
    }

    def write(n: Int): Result[JValue] = Success(JInt(n))
  }

  implicit val longFormats: Formats[Long] = new Formats[Long] {
    def read(j: JValue): Result[Long] = j match {
      case JString(str) => try (str.toLong.success) catch {
        case e => "Expected a number value but %s".format(j.toString).wrapNel.failure
      }
      case JInt(num) => num.longValue.success
      case _ => "Expected a json number but %s".format(j.toString).wrapNel.failure
    }

    def write(n: Long): Result[JValue] = JInt(n).success
  }

  implicit val floatFormats: Formats[Float] = new Formats[Float] {
    def read(j: JValue): Result[Float] = j match {
      case JString(str) => try (str.toFloat.success) catch {
        case e => "Expected a floating number value but %s".format(j.toString).wrapNel.failure
      }
      case JDouble(num) => num.floatValue.success
      case _ => "Expected a json floating number but %s".format(j.toString).wrapNel.failure
    }

    def write(n: Float): Result[JValue] = JDouble(n).success
  }

  implicit val doubleFormats: Formats[Double] = new Formats[Double] {
    def read(j: JValue): Result[Double] = j match {
      case JString(str) => try (str.toDouble.success) catch {
        case e => "Expected a floating number value but %s".format(j.toString).wrapNel.failure
      }
      case JDouble(num) => num.success
      case _ => "Expected a json floating number but %s".format(j.toString).wrapNel.failure
    }

    def write(n: Double): Result[JValue] = JDouble(n).success
  }

  implicit val stringFormats: Formats[String] = new Formats[String] {
    def read(j: JValue): Result[String] = j match {
      case JString(str) => str.success
      case JInt(num) => num.toString.success
      case JDouble(num) => num.toString.success
      case _ => "Expected json string but %s".format(j.toString).wrapNel.failure
    }

    def write(s: String): Result[JValue] = JString(s).success
  }

  implicit def optionReads[A: Reads]: Reads[Option[A]] =
      new Reads[Option[A]] {
    def read(j: JValue): Result[Option[A]] = j match {
      case JNothing | JNull => None.success
      case json =>
        fromJson[A](json).fold(nel => nel.failure, x => Some(x).success)
    }
  }

  implicit def optionWrites[A: Writes]: Writes[Option[A]] =
      new Writes[Option[A]] {
    def write(o: Option[A]): Result[JValue] = o match {
      case Some(a) => toJson(a).success
      case None => JNothing.success
    }
  }

  implicit def optionFormats[A: Formats]: Formats[Option[A]] =
      new Formats[Option[A]] {
    def read(j: JValue): Result[Option[A]] = optionReads[A].read(j)

    def write(o: Option[A]): Result[JValue] = optionWrites[A].write(o)
  }

  implicit def mapReads[A: Reads]: Reads[Map[String, A]] =
      new Reads[Map[String, A]] {
    def read(j: JValue): Result[Map[String, A]] = j match {
      case JObject(fields) => {
        val results = fields.map {
          case JField(k, v) => (k -> fromJson[A](v))
        }
        val errs = results.flatMap {
          case (k, Failure(nel)) => nel.list
          case _ => Nil
        }
        if (errs.isEmpty) results.map {
          case (k, Success(r)) => (k -> r)
        }.toMap.success
        else errs.toNel.fold(nel => nel.failure, throw new AssertionError)
      }
      case _ => "Expected json object but %s".format(j.toString).wrapNel.failure
    }
  }

  implicit def mapWrites[A: Writes]: Writes[Map[String, A]] =
      new Writes[Map[String, A]] {
    def write(m: Map[String, A]): Result[JValue] = JObject(m.toList.map {
      case (s, a) => JField(s, toJson(a))
    }).success
  }

  implicit def mapFormats[A: Formats]: Formats[Map[String, A]] =
      new Formats[Map[String, A]] {
    def read(j: JValue): Result[Map[String, A]] = mapReads[A].read(j)

    def write(m: Map[String, A]): Result[JValue] = mapWrites[A].write(m)
  }

  implicit def seqReads[A: Reads]: Reads[Seq[A]] = new Reads[Seq[A]] {
    def read(j: JValue): Result[Seq[A]] = j match {
      case JArray(list) => {
        val results = list.map(fromJson(_))
        val errs = results.flatMap {
          case Failure(nel) => nel.list
          case _ => Nil
        }
        if (errs.isEmpty) {
          Success(results.collect {
            case Success(r) => r
          })
        } else errs.toNel.fold(nel => nel.failure, throw new AssertionError)
      }
      case _ => "Expected json array but %s".format(j.toString).wrapNel.failure
    }
  }

  implicit def seqWrites[A: Writes]: Writes[Seq[A]] = new Writes[Seq[A]] {
    def write(seq: Seq[A]): Result[JValue] = JArray(seq.map(toJson(_)).toList).success
  }

  implicit def seqFormats[A: Formats]: Formats[Seq[A]] = new Formats[Seq[A]] {
    def read(j: JValue): Result[Seq[A]] = seqReads[A].read(j)

    def write(seq: Seq[A]): Result[JValue] = seqWrites[A].write(seq)
  }
}
