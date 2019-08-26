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

package com.ixibot.command;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Command option base class.
 *
 * @param <T> Type of value parsed by this option.
 * @author Ryan Porterfield
 */
@RequiredArgsConstructor
@Getter(AccessLevel.PACKAGE)
abstract class Option<T> {
    /** Length of columns in help message. */
    private static final int COLUMN_LENGTH = 24;
    /** GNU long option prefix. */
    private static final String GNU_PREFIX = "--";
    /**
     * Value returned by {@link Option#match(String)} if argument is not a match.
     */
    private static final int NON_MATCH = -1;
    /** POSIX short option prefix. */
    private static final String POSIX_PREFIX = "-";

    /** POSIX long option and option name. */
    @NonNull
    private final String longOption;
    /** GNU short option. */
    private final char shortOption;
    /** Number of parameters consumed by this option. */
    private final int parameterCount;
    /** Option about message for help text. */
    @NonNull
    private final String aboutText;

    /**
     * Get GNU style long option.
     *
     * @return GNU style long option
     */
    /* default */ String getLongOptionText() {
        return GNU_PREFIX + longOption;
    }

    /**
     * Get POSIX style short option.
     *
     * @return POSIX style short option
     */
    /* default */ String getShortOptionText() {
        return POSIX_PREFIX + shortOption;
    }

    /**
     * Get space between option and help text.
     *
     * @param optionLength How long the option text is
     * @return space between option and help text
     */
    /* default */ String getSpace(final int optionLength) {
        final StringBuilder stringBuilder = new StringBuilder(" ");

        for (int i = 0; i < COLUMN_LENGTH - (optionLength + 1); ++i) {
            stringBuilder.append(' ');
        }

        return stringBuilder.toString();
    }

    /**
     * Check if argument matches this option.
     *
     * @param argument Argument passed to command.
     * @return number of parameters consumed by this option if argument matches, otherwise
     *         NON_MATCH.
     * @see Option#NON_MATCH
     */
    /* default */ int match(@NonNull final String argument) {
        final int matchResult;

        if (argument.startsWith(GNU_PREFIX)) {
            final String strippedArgument = argument.substring(GNU_PREFIX.length());

            matchResult = matchLongOption(strippedArgument);
        } else if (argument.startsWith(POSIX_PREFIX)) {
            // This if block has to go after the long option if block because "--" will be matched
            // by this check.
            final String strippedArgument = argument.substring(POSIX_PREFIX.length());

            matchResult = matchShortOption(strippedArgument);
        } else {
            matchResult = matchLongOption(argument);
        }

        return matchResult;
    }

    /**
     * Check if argument matches the GNU long form of this option.
     *
     * @param argument Stripped argument passed to command.
     * @return number of parameters consumed by this option if argument matches, otherwise
     *         NON_MATCH.
     * @see Option#NON_MATCH
     */
    private int matchLongOption(@NonNull final String argument) {
        return (argument.equals(longOption)) ? parameterCount : NON_MATCH;
    }

    /**
     * Check if argument matches the POSIX short form of this option.
     *
     * @param argument Stripped argument passed to command.
     * @return number of parameters consumed by this option if argument matches, otherwise
     *         NON_MATCH.
     * @see Option#NON_MATCH
     */
    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    private int matchShortOption(@NonNull final String argument) {
        final int matchResult;

        if (argument.length() == 1) {
            // If length is 1, we can either match or not match
            matchResult = (argument.charAt(0) == shortOption) ? parameterCount : NON_MATCH;
        } else {
            /*
             * If length is < 1 this check fails and returns NON_MATCH.
             * If length is > 1 multiple short options were passed together, (IE. ps -aux) and we
             *  check if one of those arguments matches this option.
             */
            matchResult = (argument.indexOf(shortOption) > -1) ? parameterCount : NON_MATCH;
        }

        return matchResult;
    }

    /**
     * Parse parameters to a matched option.
     *
     * @param parameters List of parameters to the option.
     * @return parsed value.
     * @throws IllegalArgumentException if length of parameters is different from {@link
     *                                  Option#parameterCount}
     */
    /* default */ abstract T parse(@NonNull final String... parameters)
            throws IllegalArgumentException;
}
