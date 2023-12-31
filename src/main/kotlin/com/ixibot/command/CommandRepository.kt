package com.ixibot.command

/**
 * A command repository is responsible for parsing valid commands.
 *
 * @author Ixirsii <ixirsii@ixirsii.tech>
 */
class CommandRepository(
    /** The prefix used so the bot knows what's a command and what's not. */
    private val commandPrefix: String,
) {
    /** List of valid (registered) commands. */
    private val commands: MutableSet<Command<*, *>> by lazy { LinkedHashSet() }

    /**
     * Check if input is a command this repository recognizes.
     *
     * Note that this only checks if the input starts with the command prefix, it doesn't check the list of
     * registered commands to see if the command is valid.
     *
     * @param input The text input by the user.
     * @return true if input looks like a command, otherwise false.
     */
    fun isCommand(input: String): Boolean {
        return input.startsWith(commandPrefix)
    }

    /**
     * Parse the input into an internal bet event which can be published to the event bus.
     *
     * @param input The text input by the user.
     * @return internal bot event if the command is valid.
     * @throws IllegalArgumentException if the command is not registered with the repository.
     */
    @Throws(IllegalArgumentException::class)
    fun parse(input: String): Any {
        val trimmedInput: String = input.substring(commandPrefix.length)
        val commandArgsPair: Pair<String, String> = splitCommand(trimmedInput)
        val arguments: List<String> = splitArguments(commandArgsPair.second)
        val command: Command<*, *>? = findCommand(commandArgsPair.first)

        require(command != null) {
            "Command \"${commandArgsPair.first}\" is not registered"
        }

        return command.parse(arguments)
    }

    /**
     * Register a command with the repository.
     *
     * @param command Command to register.
     */
    fun register(command: Command<*, *>) {
        commands.add(command)
    }

    /**
     * Remove/unregister a command from the repository.
     *
     * @param command Command to unregister.
     * @return result of {@link Set#remove}.
     */
    fun unregister(command: Command<*, *>): Boolean {
        return commands.remove(command)
    }

    /**
     * Remove/unregister a command from the repository.
     *
     * @param commandName Name of the command to unregister.
     * @return result of {@link Set#remove}.
     */
    fun unregister(commandName: String): Boolean {
        return commands.removeIf { command -> command.match(commandName) }
    }

    /**
     * Find command in the repository by its name.
     *
     * @param commandName Name of the command to find.
     * @return command with name commandName if it exists, otherwise null.
     */
    private fun findCommand(commandName: String): Command<*, *>? {
        return commands.find { command -> command.match(commandName) }
    }

    /**
     * Tokenize command options and arguments.
     *
     * @param argumentString Options and arguments string.
     * @return array of separated options and arguments.
     * @throws IllegalArgumentException on incorrectly formatted arguments.
     */
    @Throws(IllegalArgumentException::class)
    private fun splitArguments(argumentString: String): List<String> {
        val argumentList: MutableList<String> = ArrayList()
        var i = 0
        while (i < argumentString.length) {
            val substring: String = argumentString.substring(i)
            i += if (substring.startsWith("\"")) {
                val index = substring.indexOf('"', 1)
                require(index != -1) {
                    "Unterminated quote found in $substring"
                }
                val token = substring.substring(1, index)
                argumentList.add(token)
                index + 2
            } else {
                val spaceIndex = substring.indexOf(' ')
                val index = if (spaceIndex == -1) substring.length else spaceIndex
                val token = substring.substring(0, index)
                argumentList.add(token)
                index + 1
            }
        }

        return argumentList
    }

    /**
     * Split user input into a pair of command and argument string.
     *
     * @param input  The text input by the user.
     * @return Pair of command name and argument string.
     */
    private fun splitCommand(input: String): Pair<String, String> {
        val index: Int = input.indexOf(' ')

        return if (index == -1) {
            Pair(input, "")
        } else {
            Pair(input.substring(0, index), input.substring(index))
        }
    }
}
