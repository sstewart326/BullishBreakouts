package com.bullish.breakouts.adapter.twilio

import com.bullish.breakouts.adapter.MessagingAdapter
import com.bullish.breakouts.config.Properties
import com.twilio.Twilio
import com.twilio.`type`.PhoneNumber
import com.twilio.rest.api.v2010.account.Message
import play.api.Logging

import scala.concurrent.{ExecutionContext, Future}

class TwilioAdapter extends MessagingAdapter with Logging {

  override def send( message: String, toNumbers: Seq[String] )(implicit ec: ExecutionContext): Future[Boolean] = {
    // Commented out for now to avoid charges
    // Twilio.init( Properties.twilioAccountSid, Properties.twilioAuthToken )

    def create( to: PhoneNumber, from: PhoneNumber ) = {
      Future {
        // Commented out for now to avoid charges
        // Message.creator( to, from, message ).create()
        true
      }.recover {
        case ex: Throwable => {
          logger.error( "Error occurred during Twilio create message call", ex )
          false
        }
      }
    }

    logger.info( s"Sending alert to s${toNumbers.size} numbers" )
    val responses =
      toNumbers.map( num => {
        val to = new PhoneNumber( num )
        val from = new PhoneNumber( Properties.twilioNumber )
        create( to, from )
      })
    Future.sequence( responses ).map( _.forall(_ == true) )
  }

}
