package org.sazabi.util

trait OpsImplicits extends ToBitSetOps with ToAllBytesOps

trait TypeClassImplicits extends BitSetTypeClasses with ScalendarTypeClasses

object ops extends OpsImplicits
object typeClasses extends TypeClassImplicits

object all extends OpsImplicits with TypeClassImplicits
