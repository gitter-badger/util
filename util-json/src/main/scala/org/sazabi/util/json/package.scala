package org.sazabi.util

import org.json4s.JValue
import org.json4s.scalaz.JsonScalaz.JSONW

import _root_.scalaz._
import std.option._
import optionSyntax._
import std.string._
import syntax.equal._
import syntax.id._
import syntax.validation._

package object json {
  def toJSONW[A](f: A => JValue): JSONW[A] = new JSONW[A] {
    def write(value: A): JValue = f(value)
  }
}
