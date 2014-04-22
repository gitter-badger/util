package org.sazabi.util.zk

import com.twitter.zk.ZNode

trait OpsImplicits extends ToZkClientOps
trait TypeClassImplicits extends ZNodeTypeClasses

object ops extends OpsImplicits
object typeClasses extends TypeClassImplicits

object all extends OpsImplicits with TypeClassImplicits
