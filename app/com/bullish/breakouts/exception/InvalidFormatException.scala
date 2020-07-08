package com.bullish.breakouts.exception

class InvalidFormatException(private val message: String = "",
                             private val cause: Throwable = None.orNull) extends RuntimeException(message, cause) {}
