package com.ixibot.exception

sealed class DatabaseException(message: String, cause: Throwable) : Exception(message, cause) {
    class SQLException(message: String, cause: Throwable) : DatabaseException(message, cause)
}