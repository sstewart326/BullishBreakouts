package com.bullish.breakouts.services

import com.bullish.breakouts.services.impl.TextServiceImpl
import com.google.inject.ImplementedBy

import scala.concurrent.Future

@ImplementedBy(classOf[TextServiceImpl])
trait TextService {

  def send( message: String, toNumbers: Seq[String] ): Future[Boolean]

}
