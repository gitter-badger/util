package org.sazabi.util.zk

import com.twitter.zk.ZNode

import scalaz._

class ZNodeOps(val value: ZNode) extends AnyVal {
  def parentNodePath: String = value.path.lastIndexOf('/') match {
    case i if (i == -1 || i == value.path.length - 1) => value.path
    case i if i == 0 => "/"
    case i => value.path.substring(0, i)
  }
}

trait ToZNodeOps {
  implicit val toZNodeOps: ZNode => ZNodeOps = new ZNodeOps(_)
}

trait ZNodeTypeClasses {
  implicit val zNodeOrder: Order[ZNode] = Order.orderBy(_.path)
  implicit val zNodeShow: Show[ZNode] = Show.showA
}
