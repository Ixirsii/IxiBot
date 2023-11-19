package com.ixibot.command

import com.ixibot.event.CommandEvent

private const val EQUALS: Char = '='

/** TODO: Rename "sub"-arguments to parameters
 * Base class for arguments.
 *
 * @param <T> Type of value parsed by this argument.
 * @param <E> The type of event constructed by the consumer.
 * @param <B> A builder/accumulator type which can be used to construct an E.
 * @author Ixirsii <ixirsii@ixirsii.tech>
 */
internal abstract class Argument<out T, E : CommandEvent<E, B>, B : CommandEvent.Builder<E, B>>(
    /** About message for help text. */
    internal val aboutText: String,
    /** Consume parsed value and accumulate it into event. */
    private val accumulate: (B, T) -> B,
    /** Argument's name. */
    internal val name: String,
    /** Function which parses parameters into values. */
    private val parser: (String, List<String>) -> T,
) {

    /**
     * Check if input matches this argument.
     *
     * @param input Input passed to command.
     * @return `true` if the input matches this argument, otherwise `false`.
     */
    protected abstract fun match(input: String): Boolean

    /**
     * Consume argument input and any parameters passed to it.
     *
     * @param accumulator Builder/accumulator which consumes arguments and constructs an event.
     * @param input Argument call. This will be:
     *              <ul>
     *                  <li><bold>--argument</bold> if this is an optional argument</li>
     *                  <li><bold>argumentName=</bold> if this is a positional argument with the name passed</li>
     *                  <li><bold>empty</bold> if this is a positional argument without the name passed.</li>
     *              </ul>
     * @param parameters List of parameters passed to the argument.
     * @return
     */
    fun consume(accumulator: B, input: String, parameters: List<String>): B {
        val value: T = parse(input, parameters)

        return accumulate(accumulator, value)
    }

    /**
     * Check if input matches this argument.
     *
     * @param input Input passed to command.
     * @return `true` if input matches this argument, otherwise `false`.
     */
    fun isMatch(input: String): Boolean {
        if (input.contains(EQUALS)) {
            val namedArgument: String = input.substring(0, input.indexOf(EQUALS))

            return name == namedArgument
        }

        return match(input)
    }

    /**
     * Parse input into values.
     *
     * @param input Call to argument. IE. --option, --option=value, namedParameter=value
     * @param parameters Parameters passed to this argument.
     * @return parsed value.
     */
    private fun parse(input: String, parameters: List<String>): T {
        val args: List<String> = if (input.contains(EQUALS)) {
            val values: String = input.substring(input.indexOf("=") + 1)

            // TODO: Tokenize function which skips commas inside of quotations
            values.split(',') + parameters
        } else {
            parameters
        }

        return parser(name, args)
    }
}
