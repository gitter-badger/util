package com.twitter.finagle.redis.protocol

import com.twitter.finagle.redis.util.StringToChannelBuffer

import org.jboss.netty.buffer.ChannelBuffer

/**
 * PEXPIRE.
 */
case class PExpire(key: ChannelBuffer, millis: Long) extends StrictKeyCommand {
  def command = "PEXPIRE"

  RequireClientProtocol(millis > 0, "Milliseconds must be greater than 0")

  def toChannelBuffer = RedisCodec.toUnifiedFormat(Seq(
    StringToChannelBuffer(command),
    key,
    StringToChannelBuffer(millis.toString)))
}

/**
 * PTTL.
 */
case class PTtl(key: ChannelBuffer) extends StrictKeyCommand {
  def command = "PTTL"

  def toChannelBuffer = RedisCodec.toUnifiedFormat(Seq(
    StringToChannelBuffer(command), key))
}
