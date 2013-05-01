package org.sazabi.util.bijection

trait Bijections
  extends Base58Bijections
  with UUIDBijections

object all extends Bijections
