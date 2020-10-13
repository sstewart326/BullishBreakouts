package com.bullish.breakouts.config

import com.typesafe.config.{ConfigException, ConfigFactory}

object Properties {

  private val config = ConfigFactory.load()

  val twilioAccountSid: String = config.getString( "twilio.account_sid" )
  val twilioAuthToken: String  = config.getString( "twilio.auth_token" )
  val twilioNumber: String     = config.getString( "twilio.from_number" )
  val awsAccessKey: String     = config.getString( "aws.access_key" )
  val awsSecret: String        = config.getString( "aws.secret" )
  val projectId: String        = "bullish-breakouts"
  val bucketName: String       = "bullish-breakouts-charts"
  val adminPswd: String        = "pswd"
  val allowedOrigin: Option[String] = {
    if ( config.hasPath("allowed.origin") )
      Some( config.getString("allowed.origin") )
    else None
  }

}
