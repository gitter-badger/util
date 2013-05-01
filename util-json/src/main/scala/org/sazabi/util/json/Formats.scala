package org.sazabi.util.json

import org.json4s.{JArray, JDecimal, JDouble, JInt, JValue}
import org.json4s.scalaz.JsonScalaz._

import _root_.scalaz._
import syntax.std.list._
import syntax.std.option._
import syntax.validation._

trait Formats extends formats.ScalaFormats
  with formats.JavaUtilFormats

object allFormats extends Formats
