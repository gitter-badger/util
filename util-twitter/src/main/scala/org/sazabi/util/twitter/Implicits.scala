package org.sazabi.util.twitter

trait OpsImplicits extends ToTryOps with ToFutureOps

trait TypeClassImplicits extends TryTypeClasses with FutureTypeClasses

trait ToScalaImplicits extends FutureToScala

object ops extends OpsImplicits
object typeClasses extends TypeClassImplicits
object toScala extends ToScalaImplicits

object all
  extends OpsImplicits
  with TypeClassImplicits
  with ToScalaImplicits
