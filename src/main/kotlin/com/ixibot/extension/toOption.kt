package com.ixibot.extension

import arrow.core.Option
import arrow.core.none
import arrow.core.some
import java.util.*

/**
 * Extension function to convert an [Optional] into an [Option].
 */
fun <T> Optional<T>.toOption(): Option<T> {
    return if (isPresent) {
        get().some()
    } else {
        none()
    }
}
