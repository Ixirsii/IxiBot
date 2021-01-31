/*
 * Copyright (c) 2019, Ryan Porterfield
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *     1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *     3. Neither the name of the copyright holder nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.ixibot.command

import com.ixibot.event.CommandEvent

/** Length of columns in help message.  */
private const val COLUMN_LENGTH = 24

/**
 * Get space between option and help text.
 *
 * @param optionLength How long the option text is
 * @return space between option and help text
 */
fun getSpace(optionLength: Int): String {
    val stringBuilder = StringBuilder(" ")
    for (i in 0 until COLUMN_LENGTH - (optionLength + 1)) {
        stringBuilder.append(' ')
    }
    return stringBuilder.toString()
}

/**
 * Command base class.
 *
 * @param <E> Type of event emitted by this command.
 * @author Ryan Porterfield
 */
abstract class Command<out E : CommandEvent<E>> internal constructor(
    /** Command name. */
    val name: String,
    /** Command about message for help text. */
    val aboutText: String,
    /** Command format message for help text. */
    val usageText: String,
    /** List of options accepted by this command. */
    options: List<Option<Any, E>>
) {

    /** List of options accepted by this command. */
    private val options: List<Option<Any, E>>

    init {
        val help = PresenceOption(
            aboutText = "Show this help message",
            accumulate = { accumulator: E, value: Boolean -> accumulator.toBuilder().isHelp(value).build() },
            longOption = "help",
            shortOption = 'h'
        )
        val mutable: MutableList<Option<Any, E>> = mutableListOf(help)
        mutable.addAll(options)
        this.options = mutable
    }

    /**
     * Get help text.
     *
     * @return help text.
     */
    val helpMessage: String
        get() {
            val stringBuilder = StringBuilder("$aboutText\n\nUsage:\n$usageText\n\nOptions:\n")

            for (option in options) {
                stringBuilder.append(option.toString()).append('\n')
            }

            return stringBuilder.toString()
        }

    abstract fun getAccumulator(): E

    fun parse(arguments: List<String>): E {
        var accumulator: E = getAccumulator()

        for (argument in arguments) {
            for (option in options) {
                if (option.match(argument)) {
                    accumulator = option.consume(accumulator, argument)

                    break
                }
            }
        }

        return accumulator
    }

    /**
     * Check if command matches this command.
     *
     * @param command Command entered by user.
     * @return `true` if command equals this command's name, otherwise `false`.
     */
    fun match(command: String): Boolean {
        return command == name
    }
}
