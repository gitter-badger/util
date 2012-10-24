package org.sazabi.util.id

import com.twitter.logging.Logger
import com.twitter.ostrich.stats.Stats

/**
 * Timestamp based id generator.
 * Implemented based on twitter snowflake, but limited to the range of Long.
 */
class IdGenerator(val serverId: Long) {
  import IdGenerator._

  private val genCounter = Stats.getCounter("id-generator_generated")
  private val exceptionCounter = Stats.getCounter("id-generator_exceptions")

  private var sequence: Long = 0L

  private var lastTimestamp = -1L

  private val log = Logger("server")

  // check for server id
  if (serverId > maxServerId || serverId < 0L) {
    exceptionCounter.incr()
    throw new IllegalArgumentException(
      "Server-id can't be greater than %d or less than 0".format(maxServerId))
  }

  log.ifInfo("""Id generator starting. timestamp left shift %d, server-id bits %d,
    |  sequence bits %d, server-id %d""".stripMargin.format(
      timestampLeftShift, serverIdBits, sequenceBits, serverId))

  /**
   * Generates next id.
   */
  def nextId(): Long = synchronized {
    var timestamp = now

    if (lastTimestamp == timestamp) {
      sequence = (sequence + 1L) & sequenceMask
      if (sequence == 0L) {
        timestamp = tilNextMillis(lastTimestamp)
      }
    } else {
      sequence = 0L
    }

    if (timestamp < lastTimestamp) {
      log.ifError("Clock is moving backwards. Rejecting requests until %d."
        .format(lastTimestamp));
      exceptionCounter.incr()
      throw new IllegalStateException(
        "Clock moved backwards. Refusing to generate id for %d milliseconds"
          .format(lastTimestamp - timestamp));
    }

    genCounter.incr()
    lastTimestamp = timestamp
    ((timestamp - Epoch) << timestampLeftShift) |
      (serverId << serverIdShift) |
      sequence
  }

  /**
   * Returns a infinite stream of ids.
   */
  def stream: Stream[Long] = nextId() #:: stream

  protected def tilNextMillis(lastTimestamp: Long): Long = {
    var timestamp = now
    while (timestamp <= lastTimestamp) {
      timestamp = now
    }
    timestamp
  }

  protected def now: Long = System.currentTimeMillis
}

/**
 * The companion object for IdGenerator.
 */
object IdGenerator {
  /**
   * The base timestamp value.
   */
  val Epoch: Long = 1332317625842L

  private val sequenceBits = 12L
  private val sequenceMask = -1L ^ (-1L << sequenceBits)

  private val serverIdBits = 9L // from 0 to 511
  private val maxServerId = -1L ^ (-1L << serverIdBits)
  private val serverIdShift = sequenceBits

  private val timestampLeftShift = sequenceBits + serverIdBits

  def timestamp(id: Long): Long = (id >> timestampLeftShift) + Epoch

  def id(timestamp: Long): Long = (timestamp - Epoch) << timestampLeftShift
}
