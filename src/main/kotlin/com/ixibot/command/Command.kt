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

/** Help option about text.  */
private const val ABOUT_HELP = "Show this help message"

/** Length of columns in help message.  */
private const val COLUMN_LENGTH = 24

/** Options header for help text.  */
private const val OPTIONS_HEADER = "Options:"

/** Usage header for help text.  */
private const val USAGE_HEADER = "Usage:"

/** Help parameter supported by every command.  */
private val HELP = PresenceOption(
    "help",
    'h',
    ABOUT_HELP)

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
abstract class Command<E> internal constructor(
        /** Command name.  */
        val name: String,
        /** Command about message for help text.  */
        val aboutText: String,
        /** Command format message for help text.  */
        val usageText: String,
        /** List of options accepted by this command. */
        _options: List<Option<out Any>>
) {

    /** List of options accepted by this command. */
    private val options: List<Option<out Any>>

    init {
        val mutable: MutableList<Option<out Any>> = mutableListOf(HELP)
        mutable.addAll(_options)
        options = mutable
    }

    /**
     * Get help text.
     *
     * @return help text.
     */
    val helpMessage: String
        get() {
            val stringBuilder = StringBuilder(
                    String.format(
                            "%s%n%n%s%n%s%n%n%s%n",
                            aboutText,
                            USAGE_HEADER,
                            usageText,
                            OPTIONS_HEADER))

            for (option in options) {
                stringBuilder.append(option.toString()).append(System.getProperty("line.separator"))
            }

            return stringBuilder.toString()
        }

    /**
     * Parse parameters to a matched option.
     *
     * @param parameters List of parameters to the option.
     * @return Event which can be published to the event bus.
     * @throws IllegalArgumentException if length of parameters is different from expected value.
     */
    @Throws(IllegalArgumentException::class)
    abstract fun parse(arguments: List<String>): E

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
