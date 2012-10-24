package com.twitter.finagle.redis.protocol

import com.twitter.finagle.redis.util.StringToChannelBuffer

/**
 * PING.
 */
case object Ping extends Command {
  def command = "PING"

  val toChannelBuffer = RedisCodec.toUnifiedFormat(Seq(
    StringToChannelBuffer(command)))
}
