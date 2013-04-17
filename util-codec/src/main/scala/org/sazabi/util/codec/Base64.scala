package org.sazabi.util.codec

import com.twitter.util.Codec
import com.twitter.util.Base64StringEncoder

import scalaz._

trait Base64Codec extends Codec[Array[Byte], Base64String] {
  def validate(str: String): \/[Throwable, Base64String] = \/.fromTryCatch {
    val bs = Tag[String, Base64Encoded](str)
    encode(decode(bs))
  }

  override def encode(bytes: Array[Byte]): Base64String =
    Tag[String, Base64Encoded](Base64StringEncoder.encode(bytes))

  override def decode(str: Base64String): Array[Byte] =
    Base64StringEncoder.decode(str)

  def unsafeDecode(str: String): Array[Byte] =
    Base64StringEncoder.decode(str)
}

object Base64 extends Base64Codec
