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

  def newSet(key: ChannelBuffer, value: ChannelBuffer):
    SazabiClient.NewSetCommand = SazabiClient.NewSetCommand(this, key, value)

  def setExNx(key: ChannelBuffer, seconds: Long, value: ChannelBuffer):
    Future[Boolean] = newSet(key, value).ex(seconds).nx()()

  def setPxNx(key: ChannelBuffer, millis: Long, value: ChannelBuffer):
    Future[Boolean] = newSet(key, value).px(millis).nx()()

  def setXx(key: ChannelBuffer, value: ChannelBuffer): Future[Boolean] =
    newSet(key, value).xx()()

  def setExXx(key: ChannelBuffer, seconds: Long, value: ChannelBuffer):
    Future[Boolean] = newSet(key, value).ex(seconds).xx()()

  def setPxXx(key: ChannelBuffer, millis: Long, value: ChannelBuffer):
    Future[Boolean] = newSet(key, value).px(millis).xx()()
}

object SazabiClient {
  case class NewSetCommand(
      client: Client,
      key: ChannelBuffer,
      value: ChannelBuffer,
      seconds: Option[Long] = None,
      millis: Option[Long] = None,
      nxFlag: Boolean = false,
      xxFlag: Boolean = false) {
    def ex(seconds: Long) = copy(seconds = Some(seconds))

    def px(millis: Long) = copy(millis = Some(millis))

    def nx() = copy(nxFlag = true)

    def xx() = copy(xxFlag = true)

    def apply(): Future[Boolean] = {
      client.doRequest(NewSet(key, value, seconds, millis, nxFlag, xxFlag)) {
        case StatusReply(_) => Future.value(true)
        case NilMBulkReply() => Future.value(false)
      }
    }
  }
}
