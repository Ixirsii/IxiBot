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

import com.ixibot.contracts.requireAtLeastOneParameter
import com.ixibot.contracts.requireExactlyOneParameter
import com.ixibot.contracts.requireSingleCharacter
import com.ixibot.contracts.requireZeroOrOneParameters
import com.ixibot.exception.InvalidParameterCountException
import com.ixibot.exception.InvalidParameterException
import discord4j.common.util.Snowflake
import kotlin.jvm.Throws

private val FALSE_VALUES: List<String> = listOf("f", "false", "n", "no")
private val TRUE_VALUES: List<String> = listOf("t", "true", "y", "yes")
private val VALID_VALUES_MSG: String = "Valid values are false ($FALSE_VALUES) or true ($TRUE_VALUES)."

typealias Parser<T> = (name: String, parameters: String) -> T

/**
 * Parse a single value from list of parameters.
 *
 * @param name Name of argument calling this wrapper, used for logging in event of a failure.
 * @param parameters List of parameters passed to calling argument.
 * @return parsed value.
 * @throws InvalidParameterException if value can't be parsed.
 * @throws InvalidParameterCountException if more than 1 parameter is passed.
 */
@Throws(InvalidParameterException::class, InvalidParameterCountException::class)
fun booleanParser(name: String, parameters: List<String>): Boolean {
    requireZeroOrOneParameters(name, parameters)

    return when {
        parameters.isEmpty() -> {
            true
        }
        else -> {
            val value: String = parameters[0].toLowerCase()
            val isTrue: Boolean = TRUE_VALUES.contains(value)
            val isFalse: Boolean = FALSE_VALUES.contains(value)

            if (!isFalse && !isTrue) {
                throw InvalidParameterException("$name requires a boolean but got <$value>. $VALID_VALUES_MSG")
            }

            return isTrue
        }
    }
}

/**
 * Parse multiple values from list of parameters.
 *
 * @param name Name of argument calling this wrapper, used for logging in event of a failure.
 * @param parameters List of parameters passed to calling argument.
 * @param parser Function which does the actual parsing.
 * @return parsed values.
 * @throws InvalidParameterException if value can't be parsed.
 * @throws InvalidParameterCountException if no parameters are passed.
 */
@Throws(InvalidParameterException::class, InvalidParameterCountException::class)
fun <T: Any> multiValueParser(name: String, parameters: List<String>, parser: Parser<T>): List<T> {
    requireAtLeastOneParameter(name, parameters)

    return parameters.map { parser(name, it) }
}

/**
 * Parse a single value from list of parameters.
 *
 * @param name Name of argument calling this wrapper, used for logging in event of a failure.
 * @param parameters List of parameters passed to calling argument.
 * @param parser Function which does the actual parsing.
 * @return parsed value.
 * @throws InvalidParameterException if value can't be parsed.
 * @throws InvalidParameterCountException if `args.size()` doesn't equal 1.
 */
@Throws(InvalidParameterException::class, InvalidParameterCountException::class)
fun <T: Any> singleValueParser(name: String, parameters: List<String>, parser: Parser<T>): T {
    requireExactlyOneParameter(name, parameters)

    return parser(name, parameters[0])
}

/**
 *
 */
val parseByte: Parser<Byte> = { name: String, value: String ->
    try {
        value.toByte()
    } catch (nfe: NumberFormatException) {
        throw InvalidParameterException("$name requires a byte but got <$value>")
    }
}

val parseChar: Parser<Char> = { name: String, value: String ->
    requireSingleCharacter(name, value)
    value[0]
}

val parseDouble: Parser<Double> = { name: String, value: String ->
    try {
        value.toDouble()
    } catch (nfe: NumberFormatException) {
        throw InvalidParameterException("$name requires a double but got <$value>")
    }
}

val parseFloat: Parser<Float> = { name: String, value: String ->
    try {
        value.toFloat()
    } catch (nfe: NumberFormatException) {
        throw InvalidParameterException("$name requires a float but got <$value>")
    }
}

val parseInt: Parser<Int> = { name: String, value: String ->
    try {
        value.toInt()
    } catch (nfe: NumberFormatException) {
        throw InvalidParameterException("$name requires an int but got <$value>")
    }
}

val parseLong: Parser<Long> = { name: String, value: String ->
    try {
        value.toLong()
    } catch (nfe: NumberFormatException) {
        throw InvalidParameterException("$name requires a long but got <$value>")
    }
}

val parseShort: Parser<Short> = { name: String, value: String ->
    try {
        value.toShort()
    } catch (nfe: NumberFormatException) {
        throw InvalidParameterException("$name requires a short but got <$value>")
    }
}

val parseSnowflake: Parser<Snowflake> = { name: String, value: String ->
    try {
        Snowflake.of(value)
    } catch (nfe: NumberFormatException) {
        throw InvalidParameterException("$name requires a snowflake but got <$value>")
    }
}

val parseString: Parser<String> = { _: String, value: String -> value }
