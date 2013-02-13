package org.sazabi.util

import java.nio.ByteBuffer
import java.nio.charset.Charset

class BytesOps(val value: Array[Byte]) extends AnyVal {
  def asString: String = new String(value, Charset.forName("UTF-8"))

  def toInt: Int = ByteBuffer.wrap(value).getInt()

  def toLong: Long = ByteBuffer.wrap(value).getLong()
}

trait ToBytesOps {
  implicit val toBytesOps: Array[Byte] => BytesOps = new BytesOps(_)
}

class BytesStringOps(val value: String) extends AnyVal {
  def toBytes: Array[Byte] = value.getBytes(Charset.forName("UTF-8"))
}

trait ToBytesStringOps {
  implicit val toBytesStringOps: String => BytesStringOps =
    new BytesStringOps(_)
}

class BytesIntOps(val value: Int) extends AnyVal {
  def toBytes: Array[Byte] = ByteBuffer.allocate(4).putInt(value).array
}

trait ToBytesIntOps {
  implicit val toBytesIntOps: Int => BytesIntOps = new BytesIntOps(_)
}

class BytesLongOps(val value: Long) extends AnyVal {
  def toBytes: Array[Byte] = ByteBuffer.allocate(8).putLong(value).array
}

trait ToBytesLongOps {
  implicit val toBytesLongOps: Long => BytesLongOps = new BytesLongOps(_)
}

trait ToAllBytesOps extends ToBytesOps
  with ToBytesStringOps with ToBytesIntOps with ToBytesLongOps
