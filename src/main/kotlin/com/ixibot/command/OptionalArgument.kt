/*
 * Copyright (c) 2021, Ryan Porterfield
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *     1. Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *
 *     3. Neither the name of the copyright holder nor the names of its
 *        contributors may be used to endorse or promote products derived from
 *        this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 *  IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 *  TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 *  PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 *  HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 *  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 *  TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.ixibot.command

import com.ixibot.event.CommandEvent

/** GNU long option prefix.  */
private const val GNU_PREFIX = "--"

/** POSIX short option prefix.  */
private const val POSIX_PREFIX = "-"

/**
 * Base class for optional arguments.
 *
 * @param <T> Type of value parsed by this option.
 * @param <E> The type of event constructed by the consumer.
 * @param <B> A builder/accumulator type which can be used to construct an E.
 * @author Ryan Porterfield
 */
internal abstract class OptionalArgument<out T, E : CommandEvent<E, B>, B : CommandEvent.Builder<E, B>>(
    /** About message for help text.  */
    aboutText: String,
    /** Consume parsed value and accumulate it into event. */
    accumulate: (accumulator: B, value: T) -> B,
    /** POSIX long option and option name.  */
    name: String,
    /** GNU short option.  */
    internal val shortName: Char
) : Argument<T, E, B>(aboutText = aboutText, accumulate = accumulate, name = name) {
    /**
     * Check if input matches this argument.
     *
     * @param input Argument passed to command.
     * @return true if the input matches this argument, otherwise false.
     */
    override fun match(input: String): Boolean {
        return when {
            input.startsWith(GNU_PREFIX) -> {
                input == getLongOption()
            }
            input.startsWith(POSIX_PREFIX) -> {
                // This if block has to go after the long option if block because "--" will be matched by this check.
                matchShortOption(input)
            }
            else -> {
                false
            }
        }
    }

    /**
     * Check if argument matches the POSIX short form of this option.
     *
     * @param input Argument passed to command.
     * @return `true` if argument matches the short form of this option, otherwise
     * `false`
     */
    private fun matchShortOption(input: String): Boolean {
        return if (input.length == getShortOption().length) {
            // If length is 2, we can either match or not match
            input == getShortOption()
        } else {
            /*
             * If length is < 1 this check fails and returns 0.
             * If length is > 1 multiple short options were passed together, (IE. ps -ef) and we
             *  check if one of those arguments matches this option.
             */
            input.indexOf(shortName) > -1
        }
    }

    /**
     * Get GNU style long option.
     *
     * @return GNU style long option.
     */
    private fun getLongOption(): String {
        return GNU_PREFIX + name
    }

    /**
     * Get POSIX style short option.
     *
     * @return POSIX style short option.
     */
    private fun getShortOption(): String {
        return POSIX_PREFIX + shortName
    }

    /**
     * Convert option to string for logging and help text.
     *
     * @return help text for option.
     */
    override fun toString(): String {
        val option = "${getShortOption()}, ${getLongOption()}"
        return "$option${getSpace(option.length)}$aboutText."
    }
}
