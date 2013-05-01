package org.sazabi.util.bijection

import com.twitter.bijection._

import scala.language.implicitConversions

import scalaz._

class BijectionConvertOps[A](val value: A) extends AnyVal {
  def convert[B](implicit conv: Conversion[A, B]): B = conv(value)
}

trait ToBijectionConvertOps {
  implicit def toBijectionConvertOps[A](value: A): BijectionConvertOps[A] =
    new BijectionConvertOps(value)
}

object convert extends ToBijectionConvertOps
