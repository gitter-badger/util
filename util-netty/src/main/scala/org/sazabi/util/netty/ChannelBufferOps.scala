package org.sazabi.util.netty

import org.jboss.netty.buffer.ChannelBuffer
import org.jboss.netty.util.CharsetUtil.UTF_8

class ChannelBufferOps(val value: ChannelBuffer) extends AnyVal {
  def asString: String = value.toString(UTF_8)
}
