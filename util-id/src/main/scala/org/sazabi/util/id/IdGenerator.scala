package org.sazabi.util.id

/**
 * Example implementation of Generator trait.
 */
class IdGenerator(val serverId: Long) extends Generator {
  lazy val epoch = 1332317625842L
  lazy val sequenceBits = 12
  lazy val serverIdBits = 9
}
