package org.sazabi.util

import org.jboss.netty.buffer.ChannelBuffer
import org.jboss.netty.buffer.ChannelBuffers.copiedBuffer
import org.jboss.netty.util.CharsetUtil.UTF_8

/**
 * A pimped trait for String.
 */
trait StringP extends Pimped[String] {
  def toChannelBuffer: ChannelBuffer = copiedBuffer(value, UTF_8)
}

trait Strings {
  implicit def toStringP(v: String): StringP = new StringP { val value = v }
}
