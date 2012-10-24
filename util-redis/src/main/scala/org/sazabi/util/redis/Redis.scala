package org.sazabi.util.redis

import Imports._

import org.sazabi.util.AsyncPool

import com.twitter.conversions.time._
import com.twitter.finagle.builder.ClientBuilder
import com.twitter.finagle.redis.{Redis => FR, Client => RedisClient}
import com.twitter.finagle.redis.protocol._
import com.twitter.finagle.stats.OstrichStatsReceiver
import com.twitter.finagle.Service
import com.twitter.util.Future

import org.jboss.netty.buffer.ChannelBuffer

import scalaz._
import syntax.functor._
import syntax.std.option._

/**
 * Redis instance for a single redis node.
 */
abstract class Redis(host: String, port: Int, db: Int) {
  protected def asyncPool: AsyncPool

  protected def service(): Service[Command, Reply] = ClientBuilder()
    .codec(FR())
    .hosts("%s:%d".format(host, port))
    .hostConnectionLimit(1)
    .tcpConnectTimeout(3000.milliseconds)
    .retries(2)
    .reportTo(new OstrichStatsReceiver)
    .build()

  def withClient[A](f: Client => Future[A]): Future[A] =
    newClient() flatMap { c =>
      f(c) ensure { c.release() }
    }

  def apply[A](f: Client => A): Future[A] =
    newClient() flatMap { c =>
      asyncPool(f(c)) ensure { c.release() }
    }

  def newClient(): Future[Client] =
    asyncPool(new Client(service())) flatMap { c =>
      c.select(db) >| c
    }
}

/**
 * Redis client that customizes finagle-redis Client.
 */
class Client(service: Service[Command, Reply]) extends RedisClient(service) {
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

/**
 * An exception that indicates transaction failure (WATCH).
 */
class TransactionFailed extends ServerError("Transaction failed")
