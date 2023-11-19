package com.ixibot.exception

/**
 * Exception for command API when an argument parameter can't be parsed.
 *
 * @author Ixirsii <ixirsii@ixirsii.tech>
 */
class InvalidParameterException : Exception {
    constructor(message: String) : super(message)
    constructor(message: String, exception: Exception) : super(message, exception)
}
