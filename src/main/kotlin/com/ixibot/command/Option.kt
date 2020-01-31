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

import lombok.AccessLevel
import lombok.Getter

/**
 * Command option base class.
 *
 * @param <T> Type of value parsed by this option.
 * @author Ryan Porterfield
</T> */
@Getter(AccessLevel.PACKAGE)
internal abstract class Option<T>(
        /** POSIX long option and option name.  */
        private val longOption: String,
        /** GNU short option.  */
        private val shortOption: Char,
        /** Number of parameters consumed by this option.  */
        protected val parameterCount: Int,
        /** About message for help text.  */
        private val aboutText: String
) : Comparable<Option<T>> {

    companion object {
        /** GNU long option prefix.  */
        private const val GNU_PREFIX = "--"
        /** POSIX short option prefix.  */
        private const val POSIX_PREFIX = "-"
    }

    /** POSIX long option and option name.  */
    val long: String = GNU_PREFIX + longOption

    /**
     * Get POSIX style short option.
     *
     * @return POSIX style short option
     */
    val short: String = POSIX_PREFIX + shortOption

    /**
     * Check if argument matches this option.
     *
     * @param argument Argument passed to command.
     * @return number of parameters consumed by this option if argument matches, otherwise
     * NON_MATCH.
     */
    fun match(argument: String): Int {
        val match: Boolean = when {
            argument.startsWith(GNU_PREFIX) -> {
                matchLongOption(argument)
            }
            argument.startsWith(POSIX_PREFIX) -> {
                // This if block has to go after the long option if block because "--" will be matched by this check.
                matchShortOption(argument)
            }
            else -> {
                false
            }
        }
        return if (match) 1 + parameterCount else 0
    }

    /**
     * Check if argument matches the GNU long form of this option.
     *
     * @param argument Stripped argument passed to command.
     * @return `true` if argument matches the long form of this option, otherwise
     * `false`
     */
    private fun matchLongOption(argument: String): Boolean {
        return argument == longOption
    }

    /**
     * Check if argument matches the POSIX short form of this option.
     *
     * @param argument Stripped argument passed to command.
     * @return `true` if argument matches the short form of this option, otherwise
     * `false`
     */
    private fun matchShortOption(argument: String): Boolean {
        return if (argument.length == 1) { // If length is 1, we can either match or not match
            argument[0] == shortOption
        } else { /*
             * If length is < 1 this check fails and returns 0.
             * If length is > 1 multiple short options were passed together, (IE. ps -ef) and we
             *  check if one of those arguments matches this option.
             */
            argument.indexOf(shortOption) > -1
        }
    }

    /**
     * Parse parameters to a matched option.
     *
     * @param parameters List of parameters to the option.
     * @return parsed value.
     * @throws IllegalArgumentException if length of parameters is different from
     * [Option.parameterCount]
     */
    @Throws(IllegalArgumentException::class)
    abstract fun parse(vararg parameters: String?): T

    override fun compareTo(other: Option<T>): Int {
        return shortOption.compareTo(other.shortOption)
    }

    override fun toString(): String {
        val option = "$short, $long"
        val optionSpace = Command.getSpace(option.length)
        return "$option$optionSpace$aboutText."
    }
}