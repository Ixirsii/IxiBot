package com.ixibot.command

import com.ixibot.event.CommandEvent

/**
 * Base class for positional arguments.
 *
 * @param <T> Type of value parsed by this argument.
 * @param <E> The type of event constructed by the consumer.
 * @param <B> A builder/accumulator type which can be used to construct an E.
 * @author Ixirsii <ixirsii@ixirsii.tech>
 */
internal class PositionalArgument<out T, E : CommandEvent<E, B>, B : CommandEvent.Builder<E, B>>(
    /** About message for help text. */
    aboutText: String,
    /** Consume parsed value and accumulate it into event. */
    accumulate: (accumulator: B, value: T) -> B,
    /** Argument's name. */
    name: String,
    /** Function which parses arguments into values. */
    parser: (String, List<String>) -> T,
) : Argument<T, E, B>(aboutText, accumulate, name, parser) {
    /**
     * Check if input matches this argument.
     *
     * @param input Argument passed to command.
     * @return false because we use positional matching in Command.
     */
    override fun match(input: String): Boolean {
        return false
    }

    /**
     * Convert option to string for logging and help text.
     *
     * @return help text for option.
     */
    override fun toString(): String {
        return "$name${getSpace(name.length)}$aboutText."
    }
}
