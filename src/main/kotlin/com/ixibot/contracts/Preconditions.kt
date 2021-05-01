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

package com.ixibot.contracts

import com.ixibot.exception.UnrecognizedArgumentException
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Throw an [UnrecognizedArgumentException] when args does not contain exactly 1 argument.
 *
 * @param name Name of the option/argument making the check.
 * @param args List of arguments passed to the option/argument.
 * @throws UnrecognizedArgumentException when args does not contain exactly 1 argument.
 */
@Throws(UnrecognizedArgumentException::class)
fun requireExactlyOneArgument(name: String, args: List<String>) {
    if (args.size != 1) {
        throw UnrecognizedArgumentException("\"$name\" requires exactly one argument, got: <${args.size}>")
    }
}

/**
 * Throw an [UnrecognizedArgumentException] when string length does not equal 1.
 *
 * @param name Name of the option/argument making the check.
 * @param input Input string.
 * @throws UnrecognizedArgumentException when string length does not equal 1.
 */
@Throws(UnrecognizedArgumentException::class)
fun requireSingleCharacter(name: String, input: String) {
    if (input.length != 1) {
        throw UnrecognizedArgumentException("\"$name\" requires a single character, got: <$input>")
    }
}

/**
 * Throw an [UnrecognizedArgumentException] when `args` contains more than 1 element.
 *
 * @param name Name of the option/argument making the check.
 * @param args List of arguments passed to the option/argument.
 * @throws UnrecognizedArgumentException when `args` contains more than 1 element.
 */
@Throws(UnrecognizedArgumentException::class)
fun requireZeroOrOneArguments(name: String, args: List<String>) {
    if (args.size > 1) {
        throw UnrecognizedArgumentException("\"$name\" requires zero or one argument, got: <${args.size}>")
    }
}

/**
 * Throw an [UnrecognizedArgumentException] when argument is null.
 *
 * @param argument Argument to check.
 * @throws UnrecognizedArgumentException when argument is null.
 */
@ExperimentalContracts
@Throws(UnrecognizedArgumentException::class)
fun <T: Any> requireArgumentToExist(argument: T?) {
    contract { returns() implies (argument != null) }

    requireArgumentToExist(argument) {
        "Unrecognized argument $argument"
    }
}

/**
 * Throw an [UnrecognizedArgumentException] when argument is null.
 *
 * @param argument Argument to check.
 * @param lazyMessage Supplier of exception message called only if the check fails.
 * @throws UnrecognizedArgumentException when argument is null.
 */
@ExperimentalContracts
@Throws(UnrecognizedArgumentException::class)
inline fun <T: Any> requireArgumentToExist(argument: T?, lazyMessage: () -> Any) {
    contract { returns() implies (argument != null) }

    if (argument == null) {
        val message = lazyMessage()
        throw UnrecognizedArgumentException(message.toString())
    }
}
