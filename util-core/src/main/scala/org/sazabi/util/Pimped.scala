package org.sazabi.util

trait Pimped[A] {
  val value: A
}

object Pimped {
  implicit def unwrapPimped[A](p: Pimped[A]): A = p.value
}
