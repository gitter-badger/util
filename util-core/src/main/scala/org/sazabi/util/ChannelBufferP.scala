package org.sazabi.util

import org.jboss.netty.buffer.ChannelBuffer
import org.jboss.netty.util.CharsetUtil.UTF_8

/**
 * A pimped trait for ChannelBuffer.
 */
trait ChannelBufferP extends Pimped[ChannelBuffer] {
  def asString: String = value.toString(UTF_8)
}

trait ChannelBuffers {
  implicit def toChannelBufferP(v: ChannelBuffer): ChannelBufferP =
    new ChannelBufferP { val value = v }
}
