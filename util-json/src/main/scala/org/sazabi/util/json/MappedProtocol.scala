package org.sazabi.util.json

import org.json4s.JValue
import org.json4s.scalaz.JsonScalaz._

trait MappedProtocol {
  def mappedJSONR[A, B : JSONR](inv: B => A): JSONR[A] = new JSONR[A] {
    def read(json: JValue): Result[A] = fromJSON[B](json).map(inv)
  }

  def mappedJSONW[A, B : JSONW](conv: A => B): JSONW[A] = new JSONW[A] {
    def write(value: A): JValue = toJSON(conv(value))
  }

  def mappedJSON[A, B : JSONR : JSONW](conv: A => B)(inv: B => A): JSON[A] =
    new JSON[A] {
      def read(json: JValue): Result[A] = fromJSON[B](json).map(inv)

      def write(value: A): JValue = toJSON(conv(value))
    }
}
