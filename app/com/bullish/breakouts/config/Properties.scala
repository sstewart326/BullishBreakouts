package com.bullish.breakouts.config

import com.typesafe.config.{ConfigException, ConfigFactory}

object Properties {

  private val config = ConfigFactory.load()

  val twilioAccountSid: Option[String] = getString( "twilio.account_sid" )
  val twilioAuthToken: Option[String]  = getString( "twilio.auth_token" )
  val twilioNumber: Option[String]     = getString( "twilio.from_number" )
  val awsAccessKey: Option[String]     = getString( "aws.access_key" )
  val awsSecret: Option[String]        = getString( "aws.secret" )
  val projectId: String                = "breaking-bulls"
  val bucketName: String               = "breaking-bulls-charts"
  val adminPswd: String                = "pswd"
  val allowedOrigin: Option[String] = getString( "allowed.origin" )

  private def getString( propName: String ): Option[String] = {
    if ( config.hasPath( propName ) ) {
      Some( config.getString( propName ) )
    } else None
  }

}
