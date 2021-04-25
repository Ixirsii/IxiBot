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
import com.ixibot.exception.UnrecognizedArgumentException
import kotlin.jvm.Throws

private val FALSE_VALUES: List<String> = listOf("f", "false", "n", "no")
private val TRUE_VALUES: List<String> = listOf("t", "true", "y", "yes")
private val VALID_VALUES_MSG: String = "Valid values are false ($FALSE_VALUES) or true ($TRUE_VALUES)."

/**
 * Command option which is a boolean value.
 *
 * @param <E> The type of event constructed by the consumer.
 * @param <B> A builder/accumulator type which can be used to construct an E.
 * @author Ryan Porterfield
 */
internal class BooleanOption<E : CommandEvent<E, B>, B : CommandEvent.Builder<E, B>>(
    aboutText: String,
    accumulate: (accumulator: B, value: Boolean) -> B,
    longOption: String,
    shortOption: Char
) : OptionalArgument<Boolean, E, B>(
    aboutText = aboutText,
    accumulate = accumulate,
    name = longOption,
    shortName = shortOption
) {
    /**
     * If parse was called we assume the argument has been matched previously and return true.
     *
     * @param args This should usually be an empty list but occasionally may contain a single value if the user
     *                  explicitly sets the value of the flag.
     * @return true if input is empty, t, true, y, or yes, or false if input is f, false, n, or no.
     * @throws UnrecognizedArgumentException if input args are unrecognized.
     */
    @Throws(UnrecognizedArgumentException::class)
    override fun parseArgs(args: List<String>): Boolean {
        if (args.size > 1) {
            throw UnrecognizedArgumentException("Unrecognized arguments $args")
        }

        return when {
            args.isEmpty() -> {
                true
            }
            else -> {
                val value: String = args[0].toLowerCase()

                parseValue(value)
            }
        }
    }

    /**
     *
     * @throws UnrecognizedArgumentException
     */
    @Throws(UnrecognizedArgumentException::class)
    private fun parseValue(value: String): Boolean {
        val isTrue: Boolean = TRUE_VALUES.contains(value)
        val isFalse: Boolean = FALSE_VALUES.contains(value)

        if (!isFalse && !isTrue) {
            val errorMsg = "Unrecognized value passed to $name: \"$value\". $VALID_VALUES_MSG"
            throw UnrecognizedArgumentException(errorMsg)
        }

        return isTrue
    }
}
