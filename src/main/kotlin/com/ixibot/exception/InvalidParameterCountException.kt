package com.ixibot.exception

/**
 * Exception for command API when an incorrect number of parameters is passed to an argument.
 *
 * @author Ixirsii <ixirsii@ixirsii.tech>
 */
class InvalidParameterCountException : Exception {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
}
