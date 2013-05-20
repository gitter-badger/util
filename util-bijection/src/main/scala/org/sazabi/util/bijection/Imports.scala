package org.sazabi.util.bijection

trait Bijections
  extends Base58Bijections
  with BitSetBijections
  with HexBijections
  with UUIDBijections

object all extends Bijections
