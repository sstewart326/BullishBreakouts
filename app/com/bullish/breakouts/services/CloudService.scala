package com.bullish.breakouts.services

import java.io.File

import com.bullish.breakouts.domain.ChartsMetaResponse
import com.bullish.breakouts.services.impl.CloudServiceImpl
import com.google.inject.ImplementedBy

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[CloudServiceImpl])
trait CloudService {

  def uploadImage( bucket: String, path: String, image: File )( implicit ec: ExecutionContext ): Future[Boolean]

  def fetchChartsMeta( bucket: String, pageNum: Int )( implicit ec: ExecutionContext): Future[ChartsMetaResponse]

  def fetchImage( key: String, bucket: String )( implicit executionContext: ExecutionContext ): Future[File]

}
