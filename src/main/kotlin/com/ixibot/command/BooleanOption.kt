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
import kotlin.jvm.Throws

private val FALSE_VALUES: List<String> = listOf("f", "false", "n", "no")
private val TRUE_VALUES: List<String> = listOf("t", "true", "y", "yes")

/**
 * Command option which is true if present.
 *
 * @param <E> The type of event constructed by the consumer.
 * @param <B> The type of Builder used to construct the event.
 * @author Ryan Porterfield
 */
internal class BooleanOption<E : CommandEvent<E, B>, B : CommandEvent.Builder<E, B>>(
    aboutText: String,
    accumulate: (accumulator: B, value: Boolean) -> B,
    longOption: String,
    shortOption: Char
) : Option<Boolean, E, B>(
    aboutText = aboutText,
    accumulate = accumulate,
    longOption = longOption,
    shortOption = shortOption
) {

    /**
     * If parse was called we assume the argument has been matched previously and return true.
     *
     * @param input Option input which was matched by Option.match. IE --boolOpt, --boolOpt=y, -b.
     * @param inputArgs This should usually be an empty list but occasionally may contain a single value if the user
     *                  explicitly sets the value of the flag.
     * @return true if input is empty, t, true, y, or yes, or false if input is f, false, n, no.
     * @throws IllegalArgumentException if input args are unrecognized.
     */
    @Throws(IllegalArgumentException::class)
    override fun parse(input: String, inputArgs: List<String>): Boolean {
        val containsEquals: Boolean = input.contains('=')
        return if (inputArgs.isEmpty() && !containsEquals) {
            true
        } else if ((inputArgs.isNotEmpty() && containsEquals) || inputArgs.size > 1) {
            throw IllegalArgumentException("Unrecognized arguments $inputArgs")
        } else if (inputArgs.isNotEmpty()) {
            val value: String = inputArgs[0].toLowerCase()

            parseValue(value)
        } else {
            val value: String = input.substring(input.indexOf("=") + 1)

            parseValue(value)
        }
    }

    private fun parseValue(value: String): Boolean {
        val isTrue: Boolean = TRUE_VALUES.contains(value)
        val isFalse: Boolean = FALSE_VALUES.contains(value)

        if (!isFalse && !isTrue) {
            // TODO: list valid values
            throw IllegalArgumentException("Unrecognized value passed to $longOption: \"$value\".")
        }

        return isTrue
    }
}
