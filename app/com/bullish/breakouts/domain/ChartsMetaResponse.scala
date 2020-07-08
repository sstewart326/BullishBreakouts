package com.bullish.breakouts.domain

import play.api.libs.json.Json

case class ChartsMetaResponse( meta: Seq[ChartsMeta], total: Int )

case class ChartsMeta( key: String, ticker: String, date: String )

object ChartsMetaResponse {
  implicit val metaWrites = Json.writes[ChartsMeta]
  implicit val responseWrites = Json.writes[ChartsMetaResponse]
}
