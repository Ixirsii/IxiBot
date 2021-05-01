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

import com.ixibot.contracts.requireExactlyOneArgument
import com.ixibot.event.CommandEvent
import com.ixibot.exception.UnrecognizedArgumentException
import discord4j.core.`object`.util.Snowflake
import java.lang.IllegalArgumentException

/**
 * Command argument which takes a Snowflake.
 *
 * @author Ryan Porterfield
 */
internal class SnowflakeArgument<E : CommandEvent<E, B>, B : CommandEvent.Builder<E, B>> constructor(
    /** About message for help text. */
    aboutText: String,
    /** Consume parsed value and accumulate it into event. */
    accumulate: (accumulator: B, value: Snowflake) -> B,
    /** Argument name. */
    name: String
) : PositionalArgument<Snowflake, E, B>(aboutText = aboutText, accumulate = accumulate, name = name) {

    override fun parseArgs(args: List<String>): Snowflake {
        requireExactlyOneArgument(name, args)

        return try {
            Snowflake.of(args[0])
        } catch (nfe: NumberFormatException) {
            throw UnrecognizedArgumentException("$name requires a snowflake, got <${args[0]}>")
        }
    }
}
