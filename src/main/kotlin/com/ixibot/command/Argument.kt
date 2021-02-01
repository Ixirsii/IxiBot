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

/**
 * Positional argument base class.
 *
 * @param <T> Type of value parsed by this argument.
 * @author Ryan Porterfield
 */
internal abstract class Argument<out T, E : CommandEvent<E, B>, B : CommandEvent.Builder<E, B>>(
    /** About message for help text. */
    private val aboutText: String,
    /** Consume parsed value and accumulate it into event. */
    private val accumulate: (accumulator: B, value: T) -> B,
    /** Argument name. */
    private val name: String
) {

    /**
     * Parse value from argument string.
     *
     * @param argument Argument string.
     * @return value parsed from argument string.
     */
    abstract fun parse(argument: String): T

    fun consume(accumulator: B, input: String): B {
        val value: T = parse(input)

        return accumulate(accumulator, value)
    }

    fun match(argument: String): Boolean {
        if (!argument.contains('=')) {
            return true
        }

        val namedArgument: String = argument.substring(0, argument.indexOf('='))

        return name == namedArgument
    }

    /**
     * {@inheritDoc}
     */
    override fun toString(): String {
        return "$name${getSpace(name.length)}$aboutText."
    }
}
