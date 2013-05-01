package org.sazabi.util.scalendar

trait Implicits extends ScalendarBijections
  with ScalendarScalazTypeClasses
  with ScalendarJSONTypeClasses

object all extends Implicits
