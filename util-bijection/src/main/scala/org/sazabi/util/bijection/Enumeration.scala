package org.sazabi.util.bijection

import com.twitter.bijection.{AbstractInjection, Injection}

trait InjectableEnumeration { self: Enumeration =>
  implicit val enumStringInjection: Injection[Value, String] =
    new AbstractInjection[Value, String] {
      def apply(value: Value): String = value.toString

      override def invert(str: String): Option[Value] =
        try(Some(self.withName(str))) catch { case x: Throwable => None }
    }

  implicit val enumIntInjection: Injection[Value, Int] =
    new AbstractInjection[Value, Int] {
      def apply(value: Value): Int = value.id

      override def invert(id: Int): Option[Value] =
        try(Some(self.apply(id))) catch { case x: Throwable => None }
    }
}
