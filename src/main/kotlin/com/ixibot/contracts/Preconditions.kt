package com.ixibot.contracts

import com.ixibot.exception.InvalidParameterCountException
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Throw an [InvalidParameterCountException] when args does not contain exactly 1 argument.
 *
 * @param name Name of the option/argument making the check.
 * @param args List of arguments passed to the option/argument.
 * @throws InvalidParameterCountException when args does not contain exactly 1 argument.
 */
@Throws(InvalidParameterCountException::class)
fun requireAtLeastOneParameter(name: String, args: List<String>) {
    if (args.isEmpty()) {
        throw InvalidParameterCountException("\"$name\" requires at least one argument")
    }
}

/**
 * Throw an [InvalidParameterCountException] when args does not contain exactly 1 argument.
 *
 * @param name Name of the option/argument making the check.
 * @param args List of arguments passed to the option/argument.
 * @throws InvalidParameterCountException when args does not contain exactly 1 argument.
 */
@Throws(InvalidParameterCountException::class)
fun requireExactlyOneParameter(name: String, args: List<String>) {
    if (args.size != 1) {
        throw InvalidParameterCountException("\"$name\" requires exactly one argument but got: <${args.size}>")
    }
}

/**
 * Throw an [InvalidParameterCountException] when string length does not equal 1.
 *
 * @param name Name of the option/argument making the check.
 * @param input Input string.
 * @throws InvalidParameterCountException when string length does not equal 1.
 */
@Throws(InvalidParameterCountException::class)
fun requireSingleCharacter(name: String, input: String) {
    if (input.length != 1) {
        throw InvalidParameterCountException("\"$name\" requires a single character but got: <$input>")
    }
}

/**
 * Throw an [InvalidParameterCountException] when `args` contains more than 1 element.
 *
 * @param name Name of the option/argument making the check.
 * @param args List of arguments passed to the option/argument.
 * @throws InvalidParameterCountException when `args` contains more than 1 element.
 */
@Throws(InvalidParameterCountException::class)
fun requireZeroOrOneParameters(name: String, args: List<String>) {
    if (args.size > 1) {
        throw InvalidParameterCountException("\"$name\" requires zero or one argument but got: <${args.size}>")
    }
}

/**
 * Throw an [InvalidParameterCountException] when argument is null.
 *
 * @param argument Argument to check.
 * @throws InvalidParameterCountException when argument is null.
 */
@ExperimentalContracts
@Throws(InvalidParameterCountException::class)
fun <T : Any> requireArgumentToExist(argument: T?) {
    contract { returns() implies (argument != null) }

    requireArgumentToExist(argument) {
        "Unrecognized argument $argument"
    }
}

/**
 * Throw an [InvalidParameterCountException] when argument is null.
 *
 * @param argument Argument to check.
 * @param lazyMessage Supplier of exception message called only if the check fails.
 * @throws InvalidParameterCountException when argument is null.
 */
@ExperimentalContracts
@Throws(InvalidParameterCountException::class)
inline fun <T : Any> requireArgumentToExist(argument: T?, lazyMessage: () -> Any) {
    contract { returns() implies (argument != null) }

    if (argument == null) {
        val message = lazyMessage()
        throw InvalidParameterCountException(message.toString())
    }
}
