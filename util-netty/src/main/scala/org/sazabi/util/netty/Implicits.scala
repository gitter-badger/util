package org.sazabi.util.netty

import org.jboss.netty.buffer.ChannelBuffer

trait Implicits {
  implicit val toStringOps: String => StringOps = new StringOps(_)

  implicit val toChannelBufferOps: ChannelBuffer => ChannelBufferOps =
    new ChannelBufferOps(_)
}

object Implicits extends Implicits
