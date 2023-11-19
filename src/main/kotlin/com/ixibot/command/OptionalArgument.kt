package com.ixibot.command

import com.ixibot.event.CommandEvent

/** GNU long option prefix.  */
private const val GNU_PREFIX: String = "--"

/** POSIX short option prefix.  */
private const val POSIX_PREFIX: String = "-"

/**
 * Base class for optional arguments.
 *
 * @param <T> Type of value parsed by this option.
 * @param <E> The type of event constructed by the consumer.
 * @param <B> A builder/accumulator type which can be used to construct an E.
 * @author Ixirsii <ixirsii@ixirsii.tech>
 */
internal class OptionalArgument<out T, E : CommandEvent<E, B>, B : CommandEvent.Builder<E, B>>(
    /** About message for help text.  */
    aboutText: String,
    /** Consume parsed value and accumulate it into event. */
    accumulate: (B, T) -> B,
    /** POSIX long option and option name.  */
    longOption: String,
    /** Function which parses parameters into values. */
    parser: (String, List<String>) -> T,
    /** GNU short option.  */
    private val shortOption: Char,
) : Argument<T, E, B>(aboutText, accumulate, longOption, parser) {
    /**
     * Check if input matches this argument.
     *
     * @param input Argument passed to command.
     * @return `true` if the input matches this argument, otherwise `false`.
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
     * Check if input matches the short form of this option.
     *
     * @param input Argument passed to command.
     * @return `true` if input matches the short form of this option, otherwise `false`.
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
            input.indexOf(shortOption) > -1
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
        return POSIX_PREFIX + shortOption
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
