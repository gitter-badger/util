package org.sazabi.util.zk

import com.twitter.zk.ZNode

import scalaz._

trait ZNodeTypeClasses {
  implicit val zNodeOrder: Order[ZNode] = Order.orderBy(_.path)
  implicit val zNodeShow: Show[ZNode] = Show.showA
}
