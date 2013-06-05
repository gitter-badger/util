package com.twitter.finagle.redis.protocol

import com.twitter.finagle.redis.util.StringToChannelBuffer

object SazabiCommands {
  val SET = "SET"
  val PEXPIRE = "PEXPIRE"
  val PEXPIREAT = "PEXPIREAT"
  val PTTL = "PTTL"
  val PING = "PING"
}

object SazabiCommandBytes {
  val SET = StringToChannelBuffer("SET")
  val PEXPIRE = StringToChannelBuffer("PEXPIRE")
  val PEXPIREAT = StringToChannelBuffer("PEXPIREAT")
  val PTTL = StringToChannelBuffer("PTTL")
  val PING = StringToChannelBuffer("PING")
}
