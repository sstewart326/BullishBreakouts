package com.bullish.breakouts.web.action

import javax.inject.Inject
import play.api.mvc.{ActionBuilderImpl, BodyParsers, Request, Result, WrappedRequest}

import scala.concurrent.{ExecutionContext, Future}

sealed trait UserRequest

case class AuthenticatedRequest[A](request: Request[A]) extends WrappedRequest[A](request) with UserRequest

case class UnauthenticatedRequest[A](request: Request[A]) extends WrappedRequest[A](request) with UserRequest

class UserAction @Inject()(parser: BodyParsers.Default)(implicit ec: ExecutionContext) extends ActionBuilderImpl(parser) {

  //TODO
  override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] = {
    val maybeUsername = request.session.get("USERNAME")
    maybeUsername match {
      case None => {
        block( UnauthenticatedRequest(request) )
      }
      case Some(_) => {
        block( AuthenticatedRequest(request) )
      }
    }
  }
}
