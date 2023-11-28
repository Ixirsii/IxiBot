package com.ixibot

import com.ixibot.module.*
import org.koin.core.context.startKoin

/**
 * Main method.
 */
fun main() {
    startKoin {
        modules(BotModule().module, DatabaseModule().module)
    }

    IxiBot().use {
        it.init()
    }
}
