package com.twitter.finagle.redis.protocol

import com.twitter.finagle.redis.util.StringToChannelBuffer
import com.twitter.util.Time

import org.jboss.netty.buffer.ChannelBuffer

/**
 * PEXPIRE.
 */
case class PExpire(key: ChannelBuffer, millis: Long) extends StrictKeyCommand {
  def command = SazabiCommands.PEXPIRE

  RequireClientProtocol(millis > 0, "Milliseconds must be greater than 0")

  def toChannelBuffer = RedisCodec.toUnifiedFormat(Seq(
    SazabiCommandBytes.PEXPIRE,
    key,
    StringToChannelBuffer(millis.toString)))
}

/**
 * PEXPIRE.
 */
case class PExpireAt(key: ChannelBuffer, timestamp: Time) extends StrictKeyCommand {
  def command = SazabiCommands.PEXPIREAT

  RequireClientProtocol(
    timestamp != null && timestamp > Time.now,
      "Timestamp must be in the future")

  val millis = timestamp.inMilliseconds

  def toChannelBuffer = RedisCodec.toUnifiedFormat(Seq(
    SazabiCommandBytes.PEXPIREAT,
    key,
    StringToChannelBuffer(millis.toString)))
}

/**
 * PTTL.
 */
case class PTtl(key: ChannelBuffer) extends StrictKeyCommand {
  def command = SazabiCommands.PTTL

  def toChannelBuffer = RedisCodec.toUnifiedFormat(Seq(
    SazabiCommandBytes.PTTL, key))
}
