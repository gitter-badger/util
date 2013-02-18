package org.sazabi.util.twitter

trait OpsImplicits extends ToTryOps with ToFutureOps

trait TypeClassImplicits extends TryTypeClasses with FutureTypeClasses

object ops extends OpsImplicits
object typeClasses extends TypeClassImplicits

object all extends OpsImplicits with TypeClassImplicits
