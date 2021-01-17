package com.ixibot.command

import com.ixibot.data.BotConfiguration

// TODO: this
class CommandRepository(
        private val commandPrefix: String
) {
    private val commands: MutableList<Command<Any>> by lazy { ArrayList<Command<Any>>() }

    fun isCommand(str: String): Boolean {
        return str.startsWith(commandPrefix)
    }

    fun register(command: Command<Any>) {
        commands.add(command)
    }

    fun unregister(command: Command<Any>): Boolean {
        return commands.remove(command)
    }

    fun unregister(commandName: String): Boolean {
        return commands.removeIf { command -> command.match(commandName) }
    }

    private fun findCommand(str: String): Command<Any>? {
        return commands.find { command -> command.match(str) }
    }

    private fun split(str: String): Pair<String, String> {
        val strSansPrefix: String = str.substring(commandPrefix.length)
        val index: Int = str.indexOf(' ')

        return if (index == -1) {
            Pair(strSansPrefix, "")
        } else {
            Pair(strSansPrefix.substring(0, index), strSansPrefix.substring(index))
        }
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
                    String.format("Unterminated quote found in %s", substring)
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
}