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

import com.ixibot.contracts.requireExactlyOneArgument
import com.ixibot.event.CommandEvent
import com.ixibot.exception.UnrecognizedArgumentException

/**
 * Command option which is a float value.
 *
 * @param <E> The type of event constructed by the consumer.
 * @param <B> A builder/accumulator type which can be used to construct an E.
 * @author Ryan Porterfield
 */
internal class FloatOption<E : CommandEvent<E, B>, B : CommandEvent.Builder<E, B>>(
    aboutText: String,
    accumulate: (accumulator: B, value: Float) -> B,
    longOption: String,
    shortOption: Char
) : OptionalArgument<Float, E, B>(
    aboutText = aboutText,
    accumulate = accumulate,
    name = longOption,
    shortName = shortOption
) {
    /**
     * Parse parameters to a matched argument.
     *
     * @param args Additional arguments passed to the option.
     * @return parsed value.
     * @throws UnrecognizedArgumentException if argument can't be parsed as a float.
     */
    override fun parseArgs(args: List<String>): Float {
        requireExactlyOneArgument(name, args)

        try {
            return args[0].toFloat()
        } catch (nfe: NumberFormatException) {
            throw UnrecognizedArgumentException("$name requires a float, got <${args[0]}>")
        }
    }
}
