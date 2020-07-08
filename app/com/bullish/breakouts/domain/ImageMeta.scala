package com.bullish.breakouts.domain

sealed trait ImageMeta extends Product {
  val bucketName: String
  val fileName: String
}

case class ChartMeta(override val bucketName: String,
                     override val fileName: String) extends ImageMeta
