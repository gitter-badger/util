package org.sazabi.util.zk

import com.twitter.concurrent.Serialized
import com.twitter.logging.Logger
import com.twitter.util.{Duration, Future, Return, Throw, Try}
import com.twitter.zk.{NodeEvent, ZkClient, ZNode}

import org.apache.zookeeper.{CreateMode, KeeperException}
import org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE

import scala.collection.JavaConverters._

import scalaz._
import std.anyVal._
import std.option._
import std.string._
import syntax.equal._
import syntax.std.option._

/**
 * Distributed lock using ZooKeeper.
 */
trait Lock extends Serialized {
  import Lock._

  /**
   * The parent path containing the lock nodes.
   */
  protected def basePath: String

  /**
   * The key of the lock node.
   * The name of lock node is [id]_[key]_[sequence] (if Separator is "_")
   */
  protected def lockKey: String

  /**
   * ZkClient instance.
   */
  protected def zkClient: ZkClient

  /**
   * The separator for node name.
   */
  protected val Separator = "_"

  /**
   * The function that returns the next unique id.
   */
  protected def nextId(): Long

  private lazy val client: ZkClient = zkClient.withAcl(OPEN_ACL_UNSAFE.asScala)

  /**
   * Base(parent) node for locks.
   */
  private lazy val base = client("/" + basePath.stripPrefix("/").stripSuffix("/"))

  /**
   * Lock.
   */
  def apply[A](f: => A): Future[A] = serialized {
    Zk.ensureNode(client, base) flatMap(createLockNode) flatMap { node =>
      checkForLock(base, node, f) ensure {
        cleanup(node)
      }
    }
  }.flatten

  /**
   * Lock with timeout.
   * @ TODO
   */
  def withTimeout[A](timeout: Duration)(f: => A): Future[A] = serialized {
    throw new Exception("Not implemented!")
  }

  /**
   * Create lock node.
   */
  private def createLockNode(parent: ZNode): Future[ZNode] = {
    val id = nextId()
    val name = new LockNodeName(id, lockKey, "")
    val node = parent(name.toString)
    node.create(mode = CreateMode.EPHEMERAL_SEQUENTIAL) onSuccess { n =>
      log.ifDebug("Lock node is created [%s]".format(n))
    } rescue {
      case e => {
        log.error(e, "Failed to create lock node [%s]".format(node))
        cleanupErrorNode(parent, id) flatMap { _ =>
          Future.rawException(LockNodeCreationFailed(e))
        }
      }
    }
  }

  /**
   * On creatinon failure, search and delete nodes with last id.
   */
  private def cleanupErrorNode(parent: ZNode, id: Long): Future[Unit] = {
    log.debug("Cleaning up because lock node may be created")
    parent.getChildren() flatMap { s =>
      val futures = s.children.withFilter(LockNodeName(_, id)).map { n =>
        n.withZkClient(client.withRetries(3)).delete() rescue {
          case e => Future.exception(
            FatalError("Failed to cleanup %s !!!".format(n), e))
        }
      }
      Future.join(futures)
    }
  }

  /**
   * Find the node which has next smaller sequence.
   */
  private def findPreviousNode(parent: ZNode, node: ZNode):
      Future[Option[ZNode]] = Future {
    val LockNodeName(_, _, seq) = node

    parent.getChildren() map { s =>
      (s.children collect {
        case n @ LockNodeName(i, k, s) if s < seq => (s, n)
      } sortBy(_._1)).lastOption.map(_._2)
    }
  }.flatten

  /**
   * Check for lock.
   */
  private def checkForLock[A](base: ZNode, node: ZNode, f: => A): Future[A] = {
    /**
     * Watch the node which has next smaller sequence.
     */
    def watch(previous: ZNode): Future[A] = {
      previous.exists.watch() flatMap {
        case ZNode.Watch(result, update) => {
          result match {
            case Throw(e: KeeperException.NoNodeException) => {
              // gone
              checkForLock(base, node, f)
            }
            case Throw(e) => Future.exception(new LockException(ex = e))
            case Return(_) => {
              // watch now
              log.ifDebug("Watching previous node [%s]".format(previous))
              update flatMap {
                // re-enter when deleted
                case NodeEvent.Deleted(_) => checkForLock(base, node, f)
                case _ => Future.exception(new LockException(
                  ex = new IllegalStateException))
              }
            }
          }
        }
        case _ =>
          Future.exception(new LockException(ex = new IllegalStateException))
      }
    }

    findPreviousNode(base, node) flatMap (_.cata(watch, {
      log.ifDebug("Now [%s] got current lock".format(node))
      Future(f)
    }))
  }

  /**
   * Delete my lock node.
   */
  private def cleanup(node: ZNode): Future[ZNode] = {
    log.debug("Cleaning up lock nodes")
    node.withZkClient(client.withRetries(3)).delete() rescue {
      case e => Future.exception(
        FatalError("Failed to cleanup %s !!!".format(node), e))
    }
  }

  /**
   * Utils for the name of lock node.
   */
  object LockNodeName {
    /**
     * Check if name's id matches.
     */
    def apply(name: String, id: Long): Boolean = name match {
      case LockNodeName(lockId, key, sequence) if lockId === id => true
      case _ => false
    }

    def apply(node: ZNode, id: Long): Boolean = apply(node.name, id)

    /**
     * Extractor for name rule.
     */
    def unapply(name: String): Option[(Long, String, String)] = {
      val parts = name.split(Separator).toList
      parts match {
        case id :: key :: Nil if key === lockKey =>
          Try((id.toLong, key, "")).toOption
        case nodeId :: key :: seq :: Nil if key === lockKey =>
          Try((nodeId.toLong, key, seq)).toOption
        case _ => none
      }
    }

    def unapply(node: ZNode): Option[(Long, String, String)] =
      unapply(node.name)
  }

  /**
   * The name of lock node.
   */
  class LockNodeName(id: Long, key: String, sequence: String) {
    override def toString: String =
      List(id.toString, key, sequence) mkString Separator
  }
}

object Lock {
  /**
   * Exception for lock.
   */
  sealed class LockException(message: String = "Failed to lock", ex: Throwable)
    extends RuntimeException(message, ex)

  case class LockNodeUnavailable(x: Throwable) extends LockException(ex = x)

  case class LockNodeCreationFailed(x: Throwable)
    extends LockException("Failed to create a lock node", x)

  case class LockTimeout(x: Throwable) extends LockException(ex = x)

  case class FatalError(msg: String, x: Throwable)
    extends LockException(message = msg, ex = x)
}
