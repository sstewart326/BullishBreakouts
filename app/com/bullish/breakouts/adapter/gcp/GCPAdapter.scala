package com.bullish.breakouts.adapter.gcp

import java.io.File

import com.bullish.breakouts.adapter.CloudAdapter
import com.bullish.breakouts.domain.ImageMeta
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.StorageOptions
import java.nio.file.Files
import java.nio.file.Paths

import com.bullish.breakouts.config.Properties
import org.apache.commons.io.FileUtils
import java.nio.channels.Channels

import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters.IterableHasAsScala

class GCPAdapter extends CloudAdapter {

  override def listBuckets(): Seq[String] = ???

  override def uploadImage(image: File, meta: ImageMeta)(implicit ec: ExecutionContext): Future[Boolean] = {
    Future {
      val storage = StorageOptions.newBuilder.setProjectId( Properties.projectId ).build.getService
      val blobId = BlobId.of( meta.bucketName, meta.fileName )
      val blobInfo = BlobInfo.newBuilder( blobId ).build
      storage.create( blobInfo, Files.readAllBytes( Paths.get( image.getAbsolutePath ) ) )
      true
    }
  }

  override def fetchChartMeta(bucket: String)(implicit ec: ExecutionContext): Future[Vector[String]] = {
    Future {
      val storage = StorageOptions.newBuilder.setProjectId( Properties.projectId ).build.getService
      val blobs = storage.get(bucket).list()
      blobs.iterateAll().asScala.map(_.getName).toVector
    }
  }

  override def fetchImage(key: String, bucket: String)(implicit executionContext: ExecutionContext): Future[File] = {
    Future {
      val storage = StorageOptions.newBuilder.setProjectId( Properties.projectId ).build.getService
      val blob = storage.get( BlobId.of( bucket, key ) )
      val inputStream = Channels.newInputStream( blob.reader() )
      val tempFile = File.createTempFile( key, ".tmp" )
      FileUtils.copyInputStreamToFile( inputStream, tempFile )
      tempFile
    }
  }

}
