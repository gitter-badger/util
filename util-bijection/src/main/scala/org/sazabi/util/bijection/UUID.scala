package org.sazabi.util.bijection

import com.twitter.bijection._

import java.nio.ByteBuffer
import java.util.UUID

import scalaz._

trait UUIDBijections {
  private[this] val Size = 16

  implicit val uuidBytesInjection: Injection[UUID, Array[Byte]] =
    new AbstractInjection[UUID, Array[Byte]] {
      def apply(uuid: UUID): Array[Byte] = {
        val buf = ByteBuffer.wrap(new Array[Byte](Size))
        buf.putLong(uuid.getMostSignificantBits())
        buf.putLong(uuid.getLeastSignificantBits())
        buf.array()
      }

      override def invert(bytes: Array[Byte]): Option[UUID] = {
        val buf = ByteBuffer.wrap(bytes)
        if (buf.limit() < Size) None
        else {
          val upper = buf.getLong()
          val lower = buf.getLong()
          Some(new UUID(upper, lower))
        }
      }
    }

  implicit val uuidStringInjection: Injection[UUID, String] =
    new AbstractInjection[UUID, String] {
      def apply(uuid: UUID): String = uuid.toString

      override def invert(str: String): Option[UUID] = {
        try(Some(UUID.fromString(str))) catch {
          case x: Throwable => {
            logger.warning("Injection.invert[UUID, String]() failed: " +
              x.getMessage())
            None
          }
        }
      }
    }
}

object uuid extends UUIDBijections
