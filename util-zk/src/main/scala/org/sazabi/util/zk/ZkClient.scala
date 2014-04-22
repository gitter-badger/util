package org.sazabi.util.zk

import org.sazabi.util.zk.ops._

import com.twitter.util.Future
import com.twitter.zk.{ ZkClient, ZNode }

import org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE

import scala.collection.JavaConverters._

class ZkClientOps(val value: ZkClient) extends AnyVal {
  def withUnsafeAcl: ZkClient = value.withAcl(OPEN_ACL_UNSAFE.asScala)

  def ensureNode(node: ZNode): Future[ZNode] = Zk.ensureNode(value, node)
}

trait ToZkClientOps {
  implicit val ToZkClientOps: ZkClient => ZkClientOps = new ZkClientOps(_)
}
