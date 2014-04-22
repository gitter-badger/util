package org.sazabi.util.argonaut
package codecs

import argonaut._
import argonaut.Json._
import argonaut.DecodeJson._

trait ScalaCodecs {
  implicit def BigDecimalDecodeJson: DecodeJson[BigDecimal] =
    DecodeJson.optionDecoder(x =>
        x.number.map(BigDecimal(_)).orElse(
          x.string flatMap(s => tryTo(BigDecimal(s)))), "BigDecimal")

  implicit def BigDecimalEncodeJson: EncodeJson[BigDecimal] =
    EncodeJson(a => jString(a.toString))
}
