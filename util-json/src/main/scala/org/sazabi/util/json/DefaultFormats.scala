package org.sazabi.util.json

import org.json4s._

import scalaz._
import std.option._
import optionSyntax._
import std.string._
import syntax.equal._
import syntax.id._
import syntax.validation._
import syntax.std.list._

object DefaultFormats extends LowPriorityFormats

trait LowPriorityFormats {
  implicit object BooleanFormats extends Formats[Boolean] {
    def read(json: JValue): Result[Boolean] = json match {
      case JBool(b) => Success(b)
      case JInt(n) => Success(n.longValue >= 0L)
      case JDecimal(d) => Success(d.doubleValue > 0d)
      case JDouble(d) => Success(d > 0d)
      case JString(s) if s === "true" || s === "1" => Success(true)
      case JString(s) if s === "false" || s === "0"  => Success(false)
      case j => "Can't convert %s to Boolean".format(j).failNel
    }

    def write(b: Boolean): Result[JValue] = Success(JBool(b))
  }

  implicit object IntFormats extends Formats[Int] {
    def read(json: JValue): Result[Int] = json match {
      case j @ JString(str) => try (Success(str.toInt)) catch {
        case _: Throwable => "Can't convert %s to Int".format(j).failNel
      }
      case JInt(i) => Success(i.intValue)
      case JDecimal(d) => Success(d.intValue)
      case JDouble(d) => Success(d.intValue)
      case j => "Can't convert %s to Int".format(j).failNel
    }

    def write(n: Int): Result[JValue] = Success(JInt(n))
  }

  implicit object LongFormats extends Formats[Long] {
    def read(json: JValue): Result[Long] = json match {
      case j @ JString(str) => try (str.toLong.success) catch {
        case _: Throwable => "Can't convert %s to Long".format(j).failNel
      }
      case JInt(i) => Success(i.longValue)
      case JDecimal(d) => Success(d.longValue)
      case JDouble(d) => Success(d.longValue)
      case j => "Can't convert %s to Long".format(j).failNel
    }

    def write(n: Long): Result[JValue] = JInt(n).success
  }

  implicit object FloatFormats extends Formats[Float] {
    def read(json: JValue): Result[Float] = json match {
      case j @ JString(str) => try (str.toFloat.success) catch {
        case _: Throwable => "Can't convert %s to Float".format(j).failNel
      }
      case JInt(i) => Success(i.floatValue)
      case JDecimal(d) => Success(d.floatValue)
      case JDouble(d) => Success(d.floatValue)
      case j => "Can't convert %s to Float".format(j).failNel
    }

    def write(n: Float): Result[JValue] = JDouble(n).success
  }

  implicit object DoubleFormats extends Formats[Double] {
    def read(json: JValue): Result[Double] = json match {
      case j @ JString(str) => try (str.toDouble.success) catch {
        case _: Throwable => "Can't convert %s to Double".format(j).failNel
      }
      case JInt(i) => i.doubleValue.success
      case JDecimal(d) => d.doubleValue.success
      case JDouble(d) => d.success
      case j => "Can't convert %s to Double".format(j).failNel
    }

    def write(n: Double): Result[JValue] = JDouble(n).success
  }

  implicit object BigIntFormats extends Formats[BigInt] {
    def read(json: JValue): Result[BigInt] = json match {
      case JInt(i) => i.success
      case JDouble(d) => BigInt(d.longValue).success
      case JDecimal(d) => d.toBigInt().success
      case j => "Can't convert %s to BigInt".format(j).failNel
    }

    def write(i: BigInt): Result[JValue] = JInt(i).success
  }

  implicit object BigDecimalFormats extends Formats[BigDecimal] {
    def read(json: JValue): Result[BigDecimal] = json match {
      case JInt(i) => BigDecimal(i).success
      case JDouble(d) => BigDecimal(d).success
      case JDecimal(d) => d.success
      case j => "Can't convert %s to BigDecimal".format(j).failNel
    }

    def write(d: BigDecimal): Result[JValue] = JDecimal(d).success
  }

  implicit object StringFormats extends Formats[String] {
    def read(json: JValue): Result[String] = json match {
      case JString(str) => str.success
      case JInt(i) => i.toString.success
      case JDouble(d) => d.toString.success
      case JDecimal(d) => d.toString.success
      case j => "Can't convert %s to String".format(j).failNel
    }

    def write(s: String): Result[JValue] = JString(s).success
  }

  implicit def optionFormats[A: Formats]: Formats[Option[A]] = {
    Formats {
      case JNothing | JNull => None.success
      case json =>
        fromJson[A](json).fold(nel => nel.failure, x => Some(x).success)
    } {
      case Some(a) => Success(toJson(a))
      case None => Success(JNothing)
    }
  }

  implicit def mapFormats[A: Formats]: Formats[Map[String, A]] = {
    Formats {
      case JObject(fields) => {
        val results = fields.map {
          case JField(k, v) => (k -> fromJson[A](v))
        }
        val errs = results.flatMap {
          case (k, Failure(nel)) => nel.list
          case _ => Nil
        }
        if (errs.isEmpty) {
          results.map {
            case (k, Success(r)) => (k -> r)
            case _ => throw new AssertionError
          }.toMap.success
        } else {
          errs.toNel.cata(nel => nel.failure, throw new AssertionError)
        }
      }
      case j => "Expected json object but %s".format(j.toString).wrapNel.failure
    } {
      case m => JObject(m.toList.map {
        case (s, a) => JField(s, toJson(a))
      }).success
    }
  }

  implicit def seqFormats[A: Formats]: Formats[Seq[A]] = {
    Formats.apply[Seq[A]] {
      case JArray(list) => {
        val results = list.map(fromJson[A](_))
        val errs = results.flatMap {
          case Failure(nel) => nel.list
          case _ => Nil
        }
        if (errs.isEmpty) {
          Success(results.collect {
            case Success(r) => r
          })
        } else errs.toNel.cata(nel => nel.failure, throw new AssertionError)
      }
      case j => "Expected json array but %s".format(j.toString).wrapNel.failure
    } {
      case seq => JArray(seq.map(toJson(_)).toList).success
    }
  }
}
