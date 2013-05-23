package org.sazabi.util.scalendar

trait Implicits extends ScalendarScalazTypeClasses
  with ScalendarJSONTypeClasses

object all extends Implicits
