package com.bullish.breakouts.domain

import play.api.libs.json.Json

case class ChartsMetaResponse( meta: Seq[ChartsMeta] )

case class ChartsMeta( id: String )

object ChartsMetaResponse {
  implicit val metaWrites = Json.writes[ChartsMeta]
  implicit val responseWrites = Json.writes[ChartsMetaResponse]
}
