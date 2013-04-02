package org.sazabi.util.json

import com.twitter.util.Codec

import org.json4s.JValue
import org.json4s.native.JsonMethods.{compact, parse, render}

import scalaz._

trait JsonCodec extends Codec[String, JValue] {
  override def encode(str: String): JValue = parse(str)
  override def decode(js: JValue): String = compact(render(js))
}

object JsonCodec extends JsonCodec
