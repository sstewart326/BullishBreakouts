package com.bullish.breakouts.adapter

import java.io.File

import com.bullish.breakouts.adapter.gcp.GCPAdapter
import com.bullish.breakouts.domain.ImageMeta
import com.google.inject.ImplementedBy

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[GCPAdapter])
trait CloudAdapter {

  def listBuckets(): Seq[String]

  def uploadImage( image: File, meta: ImageMeta )(implicit ec: ExecutionContext ): Future[Boolean]

  def fetchChartMeta( bucket: String )( implicit ec:ExecutionContext ): Future[Vector[String]]

  def fetchImage( key: String, bucket: String )( implicit executionContext: ExecutionContext ): Future[File]

}
