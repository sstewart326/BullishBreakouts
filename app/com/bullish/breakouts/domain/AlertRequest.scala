package com.bullish.breakouts.domain

import play.api.libs.json.{Format, Json}

case class AlertRequest( message: String, numbers: Seq[String] )

object AlertRequest {
  implicit val format: Format[AlertRequest] = Json.format[AlertRequest]
}
