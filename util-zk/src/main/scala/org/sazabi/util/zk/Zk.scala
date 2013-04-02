package org.sazabi.util.zk

import org.sazabi.util.zk.ops._

import com.twitter.util.Future
import com.twitter.zk.{ZkClient, ZNode}

import org.apache.zookeeper.KeeperException

import scalaz._
import std.string._
import syntax.equal._

object Zk {
  /**
   * Ensure base/parent nodes hierarchy.
   */
  private[zk] def ensureNode(zkClient: ZkClient, node: ZNode): Future[ZNode] = {
    log.ifDebug("ensureNode(%s)".format(node))
    node.exists() rescue {
      case e: KeeperException.NoNodeException => {
        log.ifDebug("%s is not exists".format(node))
        if (node.path == node.parentNodePath) {
          log.ifDebug("Creating %s".format(node))
          node.create() rescue {
            case e: IllegalArgumentException if node.path === "/" =>
              Future.value(node)
            case e: KeeperException.NodeExistsException => Future.value(node)
          }
        } else {
          ensureNode(zkClient, zkClient.parentNode(node)) flatMap { parent =>
            log.ifDebug("Creating %s".format(node))
            node.create()
          } rescue {
            case e: KeeperException.NodeExistsException => Future.value(node)
          }
        }
      }
    } onFailure {
      case e => log.error(e, "Unexpected exception in ensureNode()")
    }
  }
}
