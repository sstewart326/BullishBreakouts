package com.bullish.breakouts.adapter.aws

import java.io.File

import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.bullish.breakouts.adapter.CloudAdapter
import com.bullish.breakouts.config.Properties
import org.apache.commons.io.FileUtils

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}


class AWSAdapter extends CloudAdapter {

  val credentials = new BasicAWSCredentials(
    Properties.awsAccessKey,
    Properties.awsSecret
  )

  val s3client = AmazonS3ClientBuilder.standard()
    .withCredentials(new AWSStaticCredentialsProvider(credentials))
    .withRegion(Regions.US_EAST_1)
    .build()

  override def listBuckets: Seq[String] = {
    s3client.listBuckets().asScala.map( _.getName )
  }

  override def uploadImage(bucket: String, path: String, image: File)( implicit ec: ExecutionContext ): Future[Boolean] = {
    Future {
      s3client.putObject(bucket, path, image)
      true
    }
  }

  override def fetchChartMeta( bucket: String )( implicit ec:ExecutionContext ): Future[Vector[String]] = {
    Future {
      val objectListing = s3client.listObjects(bucket)
      objectListing.getObjectSummaries.asScala.map(_.getKey).toVector
    }
  }

  override def fetchImage( key: String, bucket: String )( implicit executionContext: ExecutionContext ): Future[File] = {
    Future {
      val s3object = s3client.getObject(bucket, key)
      val inputStream = s3object.getObjectContent
      val tempFile = File.createTempFile(s3object.getKey, ".tmp")
      FileUtils.copyInputStreamToFile(inputStream, tempFile)
      tempFile
    }
  }
}
