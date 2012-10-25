package org.sazabi.util.redis

import com.twitter.finagle.Service
import com.twitter.finagle.redis.{Client => RClient, ServerError}
import com.twitter.finagle.redis.protocol._
import com.twitter.util.Future

import org.jboss.netty.buffer.ChannelBuffer

import scalaz._

/**
 * Redis client that customizes finagle-redis Client.
 */
class Client(service: Service[Command, Reply]) extends RClient(service) {
  /**
   * PEXPIRE command.
   */
  def pExpire(key: ChannelBuffer, millis: Long): Future[Boolean] = {
    request(PExpire(key, millis)) {
      case IntegerReply(n) => Future.value(n == 1)
    }
  }

  /**
   * PTTL command.
   */
  def pTtl(key: ChannelBuffer): Future[Option[Long]] = {
    request(PTtl(key)) {
      case IntegerReply(n) => {
        if (n != -1) Future.value(Some(n))
        else Future.value(None)
      }
    }
  }

  /**
   * PING command.
   */
  def ping(): Future[Unit] = {
    request(Ping) {
      case StatusReply(_) => Future.Unit
    }
  }

  /**
   * WATCH command.
   */
  def watch(keys: Seq[ChannelBuffer]): Future[Unit] = request(Watch(keys)) {
    case StatusReply(_) => Future.Unit
  }

  /**
   * UNWATCH command.
   */
  def unwatch(): Future[Unit] = request(UnWatch) {
    case StatusReply(_) => Future.Unit
  }

  /**
   * MULTI-EXEC transaction.
   */
  def multi(commands: Seq[Command]): Future[Seq[Reply]] = {
    val discard: PartialFunction[Throwable, Future[Seq[Unit]]] = {
      case e => request(Discard) {
        case StatusReply(_) => Future.rawException(e)
      }
    }

    def doCommands(): Future[Unit] = {
      def loop(seq: Seq[Command], f: Future[Unit]): Future[Unit] = {
        if (seq.isEmpty) f
        else {
          loop(seq.tail, f flatMap { _ =>
            request(seq.head) {
              case StatusReply(_) => Future.Unit
            }
          })
        }
      }
      loop(commands, Future.Unit)
    }

    def exec(): Future[Seq[Reply]] = request(Exec) {
      case MBulkReply(messages) => Future.value(messages)
      case NilMBulkReply() => Future.rawException(new TransactionFailed)
    }

    def start(): Future[Unit] = request(Multi) {
      case StatusReply(_) => Future.Unit
    }

    start() flatMap { _ => doCommands } rescue discard flatMap { _ => exec }
  }

  private def request[T](cmd: Command)
      (handler: PartialFunction[Reply, Future[T]]) = {
    service(cmd) flatMap (handler orElse {
      case ErrorReply(message)  => Future.exception(new ServerError(message))
      case _                    => Future.exception(new IllegalStateException)
    })
  }
}
