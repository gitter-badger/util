package com.twitter.finagle.redis

import com.twitter.finagle.ServiceFactory
import com.twitter.finagle.redis.protocol._
import com.twitter.util.{Future, Time}

import org.jboss.netty.buffer.ChannelBuffer

class SazabiClient(sf: ServiceFactory[Command, Reply])
    extends ConnectedTransactionalClient(sf) {
  def pExpire(key: ChannelBuffer, millis: Long): Future[Boolean] = {
    doRequest(PExpire(key, millis)) {
      case IntegerReply(n) => Future.value(n == 1)
    }
  }

  def pExpireAt(key: ChannelBuffer, timestamp: Time): Future[Boolean] = {
    doRequest(PExpireAt(key, timestamp)) {
      case IntegerReply(n) => Future.value(n == 1)
    }
  }

  def pTtl(key: ChannelBuffer): Future[Option[Long]] = {
    doRequest(PTtl(key)) {
      case IntegerReply(n) => {
        if (n != -1) Future.value(Some(n))
        else Future.value(None)
      }
    }
  }
  
  def ping(): Future[Unit] = {
    doRequest(Ping) {
      case StatusReply(_) => Future.Unit
    }
  }
}
