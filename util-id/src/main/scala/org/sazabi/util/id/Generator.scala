package org.sazabi.util.id

import com.twitter.logging.Logger
import com.twitter.ostrich.stats.Stats

/**
 * Id generator based on timestamps.
 * Implemented based on twitter snowflake.
 *
 * @see https://github.com/twitter/snowflake
 */
trait Generator {
  /**
   * The unique id of the server.
   */
  def serverId: Long

  /**
   * The base timestamp value.
   */
  def epoch: Long

  /**
   * The size of the sequence part in bits.
   */
  def sequenceBits: Int

  /**
   * The size of the server id part in bits.
   */
  def serverIdBits: Int

  /**
   * Generate next id.
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
      log.error("Clock is moving backwards. Rejecting requests until %d.",
        lastTimestamp)
      exceptionCounter.incr()
      throw new IllegalStateException(
        "Clock moved backwards. Refusing to generate id for %d milliseconds"
          .format(lastTimestamp - timestamp));
    }

    genCounter.incr()
    lastTimestamp = timestamp
    ((timestamp - epoch) << timestampLeftShift) |
      (serverId << serverIdLeftShift) |
      sequence
  }

  /**
   * Infinite stream of ids.
   */
  def stream: Stream[Long] = nextId() #:: stream

  /**
   * Get a timestamp from the generated id.
   */
  def timestamp(id: Long): Long = (id >> timestampLeftShift) + epoch

  /**
   * Get a minimum id on given timestamp.
   */
  def minimumId(timestamp: Long): Long = (timestamp - epoch) << timestampLeftShift

  protected def tilNextMillis(lastTimestamp: Long): Long = {
    var timestamp = now
    while (timestamp <= lastTimestamp) {
      timestamp = now
    }
    timestamp
  }

  private val sequenceMask = 1L ^ (-1L << sequenceBits)
  private val maxServerId = -1L ^ (-1L << serverIdBits)

  private val serverIdLeftShift = sequenceBits
  private val timestampLeftShift = sequenceBits + serverIdBits

  private val genCounter = Stats.getCounter("id-generator_generated")
  private val exceptionCounter = Stats.getCounter("id-generator_exceptions")

  private var sequence = 0L
  private var lastTimestamp = -1L

  private val log = Logger("id-generator")

  protected def now: Long = System.currentTimeMillis

  if (sequenceBits <= 0) {
    exceptionCounter.incr()
    throw new IllegalArgumentException("sequence-bits must be greater than 0")
  }

  if (serverIdBits <= 0) {
    exceptionCounter.incr()
    throw new IllegalArgumentException("server-id-bits must be greater than 0")
  }

  if (serverId > maxServerId || serverId < 0L) {
    exceptionCounter.incr()
    throw new IllegalArgumentException(
      "Server-id must be between 1 and %d".format(maxServerId))
  }

  log.ifInfo("""Id generator starting. timestamp left shift %d, server-id bits %d,
    |  sequence bits %d, server-id %d""".stripMargin.format(timestampLeftShift,
      serverIdBits, sequenceBits, serverId))
}
