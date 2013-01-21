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

object DefaultFormats {
  implicit val booleanFormats: Formats[Boolean] = new Formats[Boolean] {
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

  implicit val intFormats: Formats[Int] = new Formats[Int] {
    def read(json: JValue): Result[Int] = json match {
      case j @ JString(str) => try (Success(str.toInt)) catch {
        case _ => "Can't convert %s to Int".format(j).failNel
      }
      case JInt(i) => Success(i.intValue)
      case JDecimal(d) => Success(d.intValue)
      case JDouble(d) => Success(d.intValue)
      case j => "Can't convert %s to Int".format(j).failNel
    }

    def write(n: Int): Result[JValue] = Success(JInt(n))
  }

  implicit val longFormats: Formats[Long] = new Formats[Long] {
    def read(json: JValue): Result[Long] = json match {
      case j @ JString(str) => try (str.toLong.success) catch {
        case _ => "Can't convert %s to Long".format(j).failNel
      }
      case JInt(i) => Success(i.longValue)
      case JDecimal(d) => Success(d.longValue)
      case JDouble(d) => Success(d.longValue)
      case j => "Can't convert %s to Long".format(j).failNel
    }

    def write(n: Long): Result[JValue] = JInt(n).success
  }

  implicit val floatFormats: Formats[Float] = new Formats[Float] {
    def read(json: JValue): Result[Float] = json match {
      case j @ JString(str) => try (str.toFloat.success) catch {
        case _ => "Can't convert %s to Float".format(j).failNel
      }
      case JInt(i) => Success(i.floatValue)
      case JDecimal(d) => Success(d.floatValue)
      case JDouble(d) => Success(d.floatValue)
      case j => "Can't convert %s to Float".format(j).failNel
    }

    def write(n: Float): Result[JValue] = JDouble(n).success
  }

  implicit val doubleFormats: Formats[Double] = new Formats[Double] {
    def read(json: JValue): Result[Double] = json match {
      case j @ JString(str) => try (str.toDouble.success) catch {
        case _ => "Can't convert %s to Double".format(j).failNel
      }
      case JInt(i) => i.doubleValue.success
      case JDecimal(d) => d.doubleValue.success
      case JDouble(d) => d.success
      case j => "Can't convert %s to Double".format(j).failNel
    }

    def write(n: Double): Result[JValue] = JDouble(n).success
  }

  implicit val bigIntFormats: Formats[BigInt] = new Formats[BigInt] {
    def read(json: JValue): Result[BigInt] = json match {
      case JInt(i) => i.success
      case JDouble(d) => BigInt(d.longValue).success
      case JDecimal(d) => d.toBigInt().success
      case j => "Can't convert %s to BigInt".format(j).failNel
    }

    def write(i: BigInt): Result[JValue] = JInt(i).success
  }

  implicit val bigDecimalFormats: Formats[BigDecimal] = new Formats[BigDecimal] {
    def read(json: JValue): Result[BigDecimal] = json match {
      case JInt(i) => BigDecimal(i).success
      case JDouble(d) => BigDecimal(d).success
      case JDecimal(d) => d.success
      case j => "Can't convert %s to BigDecimal".format(j).failNel
    }

    def write(d: BigDecimal): Result[JValue] = JDecimal(d).success
  }

  implicit val stringFormats: Formats[String] = new Formats[String] {
    def read(json: JValue): Result[String] = json match {
      case JString(str) => str.success
      case JInt(i) => i.toString.success
      case JDouble(d) => d.toString.success
      case JDecimal(d) => d.toString.success
      case j => "Can't convert %s to String".format(j).failNel
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
