package com.ixibot.command

import com.ixibot.contracts.requireArgumentToExist
import com.ixibot.event.CommandEvent
import kotlin.contracts.ExperimentalContracts

/** Length of columns in help message.  */
private const val COLUMN_LENGTH = 24

/**
 * Get space between option and help text.
 *
 * @param optionLength How long the option text is
 * @return space between option and help text
 */
fun getSpace(optionLength: Int): String {
    val stringBuilder = StringBuilder(" ")
    for (i in 0 until COLUMN_LENGTH - (optionLength + 1)) {
        stringBuilder.append(' ')
    }
    return stringBuilder.toString()
}

/**
 * Command base class.
 *
 * @param <E> Type of event emitted by this command.
 * @author Ixirsii <ixirsii@ixirsii.tech>
 */
abstract class Command<out E : CommandEvent<E, B>, B : CommandEvent.Builder<E, B>> internal constructor(
    /** Command name. */
    val name: String,
    /** Command about message for help text. */
    private val aboutText: String,
    /** Command format message for help text. */
    private val usageText: String,
    /** List of options accepted by this command. */
    options: List<OptionalArgument<Any, E, B>>,
) {

    /** List of options accepted by this command. */
    private val options: List<OptionalArgument<Any, E, B>>

    init {
        val help = OptionalArgument(
            aboutText = "Show this help message",
            accumulate = { accumulator: B, value: Boolean -> accumulator.isHelp(value) },
            longOption = "help",
            parser = ::booleanParser,
            shortOption = 'h'
        )
        this.options = listOf(help) + options
    }

    /**
     * Get help text.
     *
     * @return help text.
     */
    val helpMessage: String
        get() {
            val stringBuilder = StringBuilder("$aboutText\n\nUsage:\n$usageText\n\nOptions:\n")

            for (option in options) {
                stringBuilder.append(option.toString()).append('\n')
            }

            return stringBuilder.toString()
        }

    abstract fun getAccumulator(): B

    /**
     * Check if command matches this command.
     *
     * @param command Command entered by user.
     * @return `true` if command equals this command's name, otherwise `false`.
     */
    fun match(command: String): Boolean {
        return command == name
    }

    // TODO: Fix names
    fun parse(arguments: List<String>): E {
        val optionsWithArguments: Map<String, Pair<OptionalArgument<Any, E, B>, List<String>>> = mapArguments(arguments)
        var accumulator: B = getAccumulator()

        for ((argument, argumentsPair) in optionsWithArguments) {
            accumulator = argumentsPair.first.consume(accumulator, argument, argumentsPair.second)
        }

        return accumulator.build()
    }

    private fun getOptionArgs(arguments: List<String>): List<String> {
        val optionArgs: MutableList<String> = ArrayList()

        for (argument in arguments) {
            if (options.find { it.isMatch(argument) } != null) {
                break
            }

            optionArgs.add(argument)
        }

        return optionArgs
    }

    @OptIn(ExperimentalContracts::class)
    private fun mapArguments(arguments: List<String>): Map<String, Pair<OptionalArgument<Any, E, B>, List<String>>> {
        val optionsWithArguments: MutableMap<String, Pair<OptionalArgument<Any, E, B>, List<String>>> = HashMap()
        var skip = 0

        for ((index, argument) in arguments.withIndex()) {
            if (skip > 0) {
                --skip
                continue
            }

            val option: OptionalArgument<Any, E, B>? = options.find { it.isMatch(argument) }

            requireArgumentToExist(option)

            if (arguments.size > index + 1) {
                val optionArgs: List<String> = getOptionArgs(arguments.subList(index + 1, arguments.size))
                skip = optionArgs.size

                optionsWithArguments[argument] = Pair(option, optionArgs)
            } else {
                optionsWithArguments[argument] = Pair(option, emptyList())
            }
        }

        return optionsWithArguments
    }
}
