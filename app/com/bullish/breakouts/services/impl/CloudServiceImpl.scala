package com.bullish.breakouts.services.impl

import java.io.File

import com.bullish.breakouts.adapter.CloudAdapter
import com.bullish.breakouts.domain.{ChartsMeta, ChartsMetaResponse, ImageMeta}
import com.bullish.breakouts.services.CloudService
import javax.inject.Inject
import play.api.Logging
import play.api.cache.AsyncCacheApi

import scala.concurrent.{ExecutionContext, Future}

class CloudServiceImpl @Inject()(cloudAdapter: CloudAdapter,
                                 cache: AsyncCacheApi) extends CloudService with Logging {

  def imageMetaCacheKey( bucket: String ) = s"imagemeta-$bucket"
  def imageCacheKey( bucket: String, key: String ) = s"image-$bucket-$key"

  override def uploadImage( image: File, imageMeta: ImageMeta )(implicit ec: ExecutionContext ): Future[Boolean] = {
    val uploadedFut = cloudAdapter.uploadImage( image, imageMeta )
    uploadedFut.map( uploaded => {
      if ( uploaded ) {
        cache.remove( imageMetaCacheKey( imageMeta.bucketName ) )
        uploaded
      }
      else uploaded
    })
  }

  override def fetchChartsMeta( bucket: String, pageNum: Int, numToFetch: Int )( implicit ec: ExecutionContext ): Future[ChartsMetaResponse] = {

    def slice( meta: Vector[String] ) = {
      val chartStart = {
        val start = (pageNum * numToFetch) - numToFetch - 1
        if ( start < 0 ) 0
        else start
      }
      val chartEnd =
        if ( meta.size - 1 >= chartStart + numToFetch ) chartStart + numToFetch
        else meta.size
      meta.slice(chartStart, chartEnd)
    }

    def buildResponse( chartsMeta: Vector[String] ) = {
      val tickerFormat = "[A-Z]+".r
      val dateFormat = "[0-9]{2}-[0-9]{2}-[0-9]{4}".r
      val key = slice(chartsMeta)
      val meta = key.map( key => {
        val tickerOpt = tickerFormat.findFirstIn(key)
        val dateOpt = dateFormat.findFirstIn(key)
        (tickerOpt, dateOpt) match {
          case ( Some(ticker), Some(date) ) => Some( ChartsMeta(key, ticker, date) )
          case _ => {
            logger.error(s"Invalid key format: $key")
            None
          }
        }
      }).flatten

      ChartsMetaResponse(meta, meta.size)
    }

    cache.get[Vector[String]]( imageMetaCacheKey( bucket ) ).flatMap( entry => entry match {
      case Some( chartsMeta ) => {
        logger.info( s"Found cached entry for key ${imageMetaCacheKey( bucket )}" )
        Future { buildResponse( chartsMeta ) }
      }
      case _ => {
        logger.info( "Cache miss. Fetching image meta..." )
        val imageMetaFut = cloudAdapter.fetchChartMeta( bucket )
        imageMetaFut.map { meta =>
          if(meta.size == 0) {
            logger.warn("There was no image to fetch")
            buildResponse(Vector.empty)
          } else {
            cache.set( imageMetaCacheKey( bucket ), meta )
            buildResponse( meta )
          }
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
