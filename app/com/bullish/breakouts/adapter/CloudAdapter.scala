package com.bullish.breakouts.adapter

import java.io.File

import com.bullish.breakouts.adapter.aws.AWSAdapter
import com.google.inject.ImplementedBy

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[AWSAdapter])
trait CloudAdapter {

  def listBuckets(): Seq[String]

  def uploadImage( bucket: String, path: String, image: File )( implicit ec: ExecutionContext ): Future[Boolean]

  def fetchChartMeta( bucket: String )( implicit ec:ExecutionContext ): Future[Vector[String]]

  def fetchImage( key: String, bucket: String )( implicit executionContext: ExecutionContext ): Future[File]

}
