package org.sazabi.util.id

import com.twitter.finagle.stats.StatsReceiver
import com.twitter.logging.Logger

/**
 * Id generator based on timestamps.
 * Implemented based on twitter snowflake.
 *
 * @see https://github.com/twitter/snowflake
 */
trait Generator { self =>
  /**
   * The unique id of the server.
   */
  def serverId: Long

  /**
   * StatsReceiver.
   */
  def statsReceiver: StatsReceiver

  /**
   * Logger.
   */
  def logger: Logger

  /**
   * The base timestamp value.
   */
  def epoch: Long

  /**
   * The size of the sequence part in bits.
   */
  def sequenceBits: Long

  /**
   * The size of the server id part in bits.
   */
  def serverIdBits: Long

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
      logger.error("Clock is moving backwards. Rejecting requests until %d.",
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
  def idToTimestamp(id: Long): Long = (id >> timestampLeftShift) + epoch

  /**
   * Get a minimum id on given timestamp.
   */
  def timestampToId(timestamp: Long): Long =
    (timestamp - epoch) << timestampLeftShift

  protected def tilNextMillis(lastTimestamp: Long): Long = {
    var timestamp = now
    while (timestamp <= lastTimestamp) {
      timestamp = now
    }
    timestamp
  }

  protected def now: Long = System.currentTimeMillis

  private val genCounter = statsReceiver.counter("id-generator_generated")
  private val exceptionCounter = statsReceiver.counter("id-generator_exceptions")

  private var sequence = 0L
  private var lastTimestamp = -1L

  if (self.sequenceBits <= 0) {
    exceptionCounter.incr()
    throw new IllegalArgumentException("sequence-bits must be greater than 0")
  }

  if (self.serverIdBits <= 0) {
    exceptionCounter.incr()
    throw new IllegalArgumentException("server-id-bits must be greater than 0")
  }

  private lazy val maxServerId = -1L ^ (-1L << serverIdBits)

  if (self.serverId > maxServerId || self.serverId < 0L) {
    exceptionCounter.incr()
    throw new IllegalArgumentException(
      "Server-id must be between 1 and %d".format(maxServerId))
  }

  private lazy val sequenceMask = 1L ^ (-1L << sequenceBits)

  private lazy val serverIdLeftShift = sequenceBits

  private lazy val timestampLeftShift = sequenceBits + serverIdBits

  logger.ifInfo("""Id generator starting. timestamp left shift %d,
    |  server-id bits %d, sequence bits %d, server-id %d""".stripMargin
      .format(timestampLeftShift, serverIdBits, sequenceBits, serverId))
}
