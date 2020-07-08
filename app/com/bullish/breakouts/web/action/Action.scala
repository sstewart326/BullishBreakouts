package com.bullish.breakouts.web.action

import com.bullish.breakouts.config.Properties
import javax.inject.Inject
import play.api.mvc.{ActionBuilderImpl, BodyParsers, Request, Result}
import play.api.mvc.Results.Forbidden

import scala.concurrent.{ExecutionContext, Future}

class UserAction @Inject()(parser: BodyParsers.Default)(implicit ec: ExecutionContext) extends ActionBuilderImpl(parser) {

  override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] = {
    block(request)
  }

}

class AdminAction @Inject()(parser: BodyParsers.Default)(implicit ec: ExecutionContext) extends ActionBuilderImpl(parser) {

  override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] = {
    request.headers.get("adminPswd") match {
      case Some(pswd) if pswd == Properties.adminPswd => block(request)
      case _ => Future.successful(Forbidden)
    }
  }

}
