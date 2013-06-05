package org.sazabi.util.json

import org.json4s.{JArray, JDecimal, JDouble, JInt, JValue}
import org.json4s.scalaz.JsonScalaz._

import _root_.scalaz._
import syntax.std.list._
import syntax.std.option._
import syntax.validation._

trait Formats extends formats.ScalaFormats
  with formats.JavaNetFormats
  with formats.JavaUtilFormats
  with formats.ScalazFormats

object allFormats extends Formats
