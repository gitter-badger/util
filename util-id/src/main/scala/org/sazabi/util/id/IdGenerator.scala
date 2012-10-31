package org.sazabi.util.id

/**
 * Timestamp based id generator.
 * Implemented based on twitter snowflake, but limited to the range of Long.
 */
class IdGenerator(val serverId: Long) {
  val Epoch: Long = 1332317625842L
  val sequenceBits = 12
  val serverIdBits = 9
}
