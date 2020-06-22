package com.bullish.breakouts.adapter

import com.bullish.breakouts.adapter.twilio.TwilioAdapter
import com.google.inject.ImplementedBy

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[TwilioAdapter])
trait MessagingAdapter {

  def send( message: String, toNumbers: Seq[String] )(implicit ec: ExecutionContext): Future[Boolean]

}
