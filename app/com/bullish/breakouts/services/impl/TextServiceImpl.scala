package com.bullish.breakouts.services.impl

import com.bullish.breakouts.adapter.MessagingAdapter
import com.bullish.breakouts.services.TextService
import javax.inject.Inject

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TextServiceImpl @Inject()(messagingAdapter: MessagingAdapter) extends TextService {

  override def send( message: String, toNumbers: Seq[String] ): Future[Boolean] = {
    messagingAdapter.send( message, toNumbers )
  }

}
