package com.bullish.breakouts.config

import com.typesafe.config.ConfigFactory

object Properties {

  private val config = ConfigFactory.load()

  val twilioAccountSid = config.getString( "twilio.account_sid" )
  val twilioAuthToken  = config.getString( "twilio.auth_token" )
  val twilioNumber     = config.getString( "twilio.from_number" )
  val awsAccessKey     = config.getString( "aws.access_key" )
  val awsSecret        = config.getString( "aws.secret" )

  val bucketName       = "bullish-breakouts"

}
