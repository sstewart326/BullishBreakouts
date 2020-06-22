package com.bullish.breakouts.services.impl

import java.io.File

import com.bullish.breakouts.adapter.CloudAdapter
import com.bullish.breakouts.domain.{ChartsMeta, ChartsMetaResponse}
import com.bullish.breakouts.services.CloudService
import javax.inject.Inject
import play.api.Logging
import play.api.cache.AsyncCacheApi

import scala.concurrent.{ExecutionContext, Future}

class CloudServiceImpl @Inject()(cloudAdapter: CloudAdapter,
                                 cache: AsyncCacheApi) extends CloudService with Logging {

  def imageMetaCacheKey( bucket: String ) = s"imagemeta-$bucket"
  def imageCacheKey( bucket: String, key: String ) = s"image-$bucket-$key"

  override def uploadImage( bucket: String, path: String, image: File )( implicit ec: ExecutionContext ): Future[Boolean] = {
    val uploadedFut = cloudAdapter.uploadImage( bucket, path, image )
    uploadedFut.map( uploaded => {
      if ( uploaded ) {
        cache.remove( imageMetaCacheKey( bucket ) )
        uploaded
      }
      else uploaded
    })
  }

  override def fetchChartsMeta( bucket: String, pageNum: Int )( implicit ec: ExecutionContext ): Future[ChartsMetaResponse] = {

    def slice( meta: Vector[String] ) = {
      val MaxPerPage = 10
      val chartStart = {
        val start = (pageNum * MaxPerPage) - MaxPerPage - 1
        if ( start < 0 ) 0
        else start
      }
      val chartEnd =
        if ( meta.size - 1 >= chartStart + MaxPerPage ) chartStart + MaxPerPage
        else meta.size - 1
      meta.slice( chartStart, chartEnd )
    }

    def buildResponse( meta: Vector[String] ) = {
      ChartsMetaResponse( meta.map( ChartsMeta(_) ) )
    }

    cache.get[Vector[String]]( imageMetaCacheKey( bucket ) ).flatMap( entry => entry match {
      case Some( chartsMeta ) => {
        logger.info( s"Found cached entry for key ${imageMetaCacheKey( bucket )}" )
        Future { buildResponse( slice(chartsMeta) ) }
      }
      case _ => {
        logger.info( "Cache miss. Fetching image meta..." )
        val imageMetaFut = cloudAdapter.fetchChartMeta( bucket )
        imageMetaFut.map { meta =>
          cache.set( imageMetaCacheKey( bucket ), meta )
          buildResponse( slice(meta) )
        }
      }
    })
  }

  override def fetchImage( key: String, bucket: String )( implicit executionContext: ExecutionContext ): Future[File] =  {
    cache.get[File]( imageCacheKey( bucket, key ) ).flatMap( entry => entry match {
      case Some( image ) => {
        logger.info( "Fetched image from cache" )
        Future{ image }
      }
      case _ => {
        logger.info( "Cache miss. Fetching image" )
        val imageFut = cloudAdapter.fetchImage( key, bucket )
        imageFut.map( cache.set( imageCacheKey( bucket, key ), _ ) )
        imageFut
      }
    })
  }

}
