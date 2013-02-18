package org.sazabi.util.redis

import Imports._

import com.twitter.conversions.time._
import com.twitter.finagle.builder.ClientBuilder
import com.twitter.finagle.redis.{Redis => FR, ServerError}
import com.twitter.finagle.redis.protocol.{Command, Reply}
import com.twitter.finagle.stats.OstrichStatsReceiver
import com.twitter.finagle.Service
import com.twitter.ostrich.stats.Stats
import com.twitter.util.{Future, FuturePool}

import java.net.InetSocketAddress

import scalaz._
import syntax.functor._

/**
 * Redis instance for a single redis node.
 */
abstract class Redis(hosts: String, db: Int) {
  protected def futurePool: FuturePool

  protected def service(): Service[Command, Reply] = ClientBuilder()
    .codec(FR())
    .hosts(hosts)
    .hostConnectionLimit(1)
    .tcpConnectTimeout(3000.milliseconds)
    .retries(2)
    .reportTo(new OstrichStatsReceiver(Stats))
    .build()

  def withClient[A](f: Client => Future[A]): Future[A] =
    newClient() flatMap { c =>
      f(c) ensure { c.release() }
    }

  def apply[A](f: Client => A): Future[A] =
    newClient() flatMap { c =>
      futurePool(f(c)) ensure { c.release() }
    }

  def newClient(): Future[Client] =
    futurePool(new Client(service())) flatMap { c =>
      c.select(db) >| c
    }
}

/**
 * An exception that indicates transaction failure (WATCH).
 */
class TransactionFailed extends ServerError("Transaction failed")
