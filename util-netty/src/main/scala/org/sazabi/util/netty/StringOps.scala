package org.sazabi.util.netty

import org.jboss.netty.buffer.ChannelBuffer
import org.jboss.netty.buffer.ChannelBuffers.copiedBuffer
import org.jboss.netty.util.CharsetUtil.UTF_8

class StringOps(val value: String) extends AnyVal {
  def toChannelBuffer: ChannelBuffer = copiedBuffer(value, UTF_8)
}
