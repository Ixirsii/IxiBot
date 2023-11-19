package com.ixibot

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class LoggingImpl(logger: Logger) : Logging {
    override val log: Logger = logger

    companion object {
        inline operator fun <reified T> invoke(): LoggingImpl {
            return LoggingImpl(LoggerFactory.getLogger(T::class.java))
        }
    }
}
