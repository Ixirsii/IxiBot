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
import com.ixibot.exception.UnrecognizedArgumentException

/**
 * Base class for arguments.
 *
 * @param <T> Type of value parsed by this argument.
 * @param <E> The type of event constructed by the consumer.
 * @param <B> A builder/accumulator type which can be used to construct an E.
 * @author Ryan Porterfield
 */
internal abstract class Argument<out T, E : CommandEvent<E, B>, B : CommandEvent.Builder<E, B>>(
    /** About message for help text. */
    internal val aboutText: String,
    /** Consume parsed value and accumulate it into event. */
    private val accumulate: (accumulator: B, value: T) -> B,
    /** Argument's name. */
    internal val name: String
) {
    private val equals: Char = '='

    /**
     * Check if input matches this argument.
     *
     * @param input Argument passed to command.
     * @return true if the input matches this argument, otherwise false.
     */
    protected abstract fun match(input: String): Boolean

    /**
     * Parse parameters to a matched option.
     *
     * @param args Additional arguments passed to the option. IE if the option takes a list of values.
     * @return parsed value.
     * @throws UnrecognizedArgumentException if input args are unrecognized.
     */
    @Throws(UnrecognizedArgumentException::class)
    protected abstract fun parseArgs(args: List<String>): T

    /**
     * Consume argument input and any sub-arguments passed to the argument.
     *
     * @param accumulator Builder/accumulator which consumes arguments and constructs an event.
     * @param input Argument call. This will be:
     *              <ul>
     *                  <li><bold>--argument</bold> if this is an optional argument</li>
     *                  <li><bold>argumentName=</bold> if this is a positional argument with the name passed</li>
     *                  <li><bold>empty</bold> if this is a positional argument without the name passed.</li>
     *              </ul>
     * @param inputArgs List of sub-arguments passed to the argument.
     * @return
     */
    fun consume(accumulator: B, input: String, inputArgs: List<String>): B {
        val value: T = parse(input, inputArgs)

        return accumulate(accumulator, value)
    }

    fun isMatch(input: String): Boolean {
        if (input.contains(equals)) {
            val namedArgument: String = input.substring(0, input.indexOf(equals))

            return name == namedArgument
        }

        return match(input)
    }

    private fun parse(input: String, inputArgs: List<String>): T {
        val args: List<String> = if (input.contains(equals)) {
            val values: String = input.substring(input.indexOf("=") + 1)

            // TODO: Tokenize function which skips commas inside of quotations
            values.split(',') + inputArgs
        } else {
            inputArgs
        }

        return parseArgs(args)
    }
}
