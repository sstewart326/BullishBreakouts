package com.bullish.breakouts.web.controllers

import com.bullish.breakouts.config.Properties
import com.bullish.breakouts.domain.AlertRequest
import com.bullish.breakouts.services.{CloudService, TextService}
import com.bullish.breakouts.web.action.UserAction
import com.typesafe.config.ConfigFactory
import javax.inject.Inject
import play.api.Logging
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

/**
 * @param cc standard controller components
 * @param exec We need an `ExecutionContext` to execute our
 * asynchronous code.  When rendering content, you should use Play's
 * default execution context, which is dependency injected.  If you are
 * using blocking operations, such as database or network access, then you should
 * use a different custom execution context that has a thread pool configured for
 * a blocking API.
 */
class BBController @Inject()(cc: ControllerComponents,
                             textService: TextService,
                             cloudService: CloudService,
                             userAction: UserAction
                            )(implicit exec: ExecutionContext) extends AbstractController(cc) with Logging {

  val config = ConfigFactory.load()

  def health = Action { Ok( "Working" ) }

  def alert = userAction.async(parse.json) { implicit request =>
    request.body.validate[AlertRequest].fold(

      _ => {
        logger.error( "Invalid json" )
        Future.successful( BadRequest )
      },

      request => {
        textService.send( request.message, request.numbers ).map( result => {
          if (result) Ok("success")
          else InternalServerError
        })
      }
    )
  }

  def fetchImage( image: String ) = userAction.async { implicit request =>
    val imageFut = cloudService.fetchImage( image, Properties.bucketName )
    imageFut.map( Ok.sendFile(_).as( "image/png" ) )
  }

  //TODO admin action
  def uploadImage = userAction.async(parse.multipartFormData) { implicit request =>
    val fileUploaded = request.body.file( "chart" ).map( file =>
       cloudService.uploadImage( Properties.bucketName, file.filename, file.ref.toFile ) )
    fileUploaded.getOrElse( Future{ false } ).map( uploaded => {
      if ( uploaded ) Ok( "uploaded" )
      else InternalServerError( "failed to upload" )
    })
  }

  def chartsMeta( pageNum: String ) = userAction.async { implicit  request =>
    val maybePageNum = Try( pageNum.toInt ).toOption
    maybePageNum match {
      case Some( pageNum ) => {
        val chartsMetaFut = cloudService.fetchChartsMeta( Properties.bucketName, pageNum )
        chartsMetaFut.map( meta => Ok( Json.toJson(meta) ).withHeaders( ("Access-Control-Allow-Origin", "http://localhost:4200") ) )
      }
      case _ => Future{ BadRequest }
    }

  }

}
