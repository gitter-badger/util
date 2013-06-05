package com.twitter.finagle.redis.protocol

import com.twitter.finagle.redis.util.StringToChannelBuffer

import org.jboss.netty.buffer.ChannelBuffer

case class NewSet(key: ChannelBuffer, value: ChannelBuffer,
    seconds: Option[Long] = None, millis: Option[Long] = None,
    nx: Boolean = false, xx: Boolean = false) extends Command {
  def command = SazabiCommands.SET

  RequireClientProtocol(seconds.filter(_ <= 0).isEmpty,
    "Seconds must be greater than 0")

  RequireClientProtocol(millis.filter(_ <= 0).isEmpty,
    "Milliseconds must be greater than 0")

  RequireClientProtocol(!nx || !xx,
    "NX and XX flags cannot be used together")

  def toChannelBuffer = {
    RedisCodec.toUnifiedFormat(
      Seq(SazabiCommandBytes.SET,
        key,
        value
      ) ++ {
        seconds.map(s => Seq(NewSet.EX, StringToChannelBuffer(s.toString)))
           .getOrElse(Seq())
      } ++ {
        millis.map(m => Seq(NewSet.PX, StringToChannelBuffer(m.toString)))
          .getOrElse(Seq())
      } ++ {
        if (nx) Seq(NewSet.NX)
        else Seq()
      } ++ {
        if (xx) Seq(NewSet.XX)
        else Seq()
      })
  }
}

object NewSet {
  val EX = StringToChannelBuffer("EX")
  val PX = StringToChannelBuffer("PX")
  val NX = StringToChannelBuffer("NX")
  val XX = StringToChannelBuffer("XX")
}
