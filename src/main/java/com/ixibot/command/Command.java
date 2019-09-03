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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.Lists;
import lombok.NonNull;
import lombok.Value;

/**
 * Command base class.
 *
 * @param <E> Type of event emitted by this command.
 * @author Ryan Porterfield
 */
public abstract class Command<E> {
    /** Help option about text. */
    private static final String ABOUT_HELP = "Show this help message";
    /** Length of columns in help message. */
    private static final int COLUMN_LENGTH = 24;
    /** Help parameter supported by every command. */
    private static final PresenceOption HELP = new PresenceOption(
            "help",
            'h',
            ABOUT_HELP);
    /** Options header for help text. */
    private static final String OPTIONS_HEADER = "Options:";
    /** Usage header for help text. */
    private static final String USAGE_HEADER = "Usage:";

    /** Command about message for help text. */
    @NonNull
    private final String aboutText;
    /** Command name. */
    @NonNull
    private final String name;
    /** List of options accepted by this command. */
    @NonNull
    private final List<Option<?>> options;
    /** Command format message for help text. */
    @NonNull
    private final String usageText;

    /**
     * Get space between option and help text.
     *
     * @param optionLength How long the option text is
     * @return space between option and help text
     */
    /* default */ static String getSpace(final int optionLength) {
        final StringBuilder stringBuilder = new StringBuilder(" ");

        for (int i = 0; i < Command.COLUMN_LENGTH - (optionLength + 1); ++i) {
            stringBuilder.append(' ');
        }

        return stringBuilder.toString();
    }

    /**
     * Default constructor.
     *
     * @param name      {@link Command#name}
     * @param options   {@link Command#options}
     * @param aboutText {@link Command#aboutText}
     * @param usageText {@link Command#usageText}
     */
    /* default */ Command(
            @NonNull final String name,
            @NonNull final Option<?>[] options,
            @NonNull final String aboutText,
            @NonNull final String usageText) {
        this.aboutText = aboutText;
        this.name = name;
        this.options = Lists.asList(HELP, options);
        this.usageText = usageText;
    }

    /**
     * Tokenize command options and arguments.
     *
     * @param argumentString Options and arguments string.
     * @return array of separated options and arguments.
     * @throws IllegalArgumentException on incorrectly formatted arguments.
     */
    /* default */ String[] argumentTokenizer(@NonNull final String argumentString)
            throws IllegalArgumentException {
        final List<String> argumentList = new ArrayList<>();

        for (int i = 0; i < argumentString.length();) {
            final String substring = argumentString.substring(i);

            if (substring.startsWith("\"")) {
                final int index = substring.indexOf('"', 1);

                if (index == -1) {
                    throw new IllegalArgumentException(String.format(
                            "Unterminated quote found in %s",
                            substring));
                }

                final String token = substring.substring(1, index);

                argumentList.add(token);
                i += (index + 2);
            } else {
                final int spaceIndex = substring.indexOf(' ');
                final int index = (spaceIndex == -1) ? substring.length() : spaceIndex;
                final String token = substring.substring(0, index);

                argumentList.add(token);
                i += (index + 1);
            }
        }

        return argumentList.toArray(new String[0]);
    }

    /**
     * Get map of Options to index that consume them.
     *
     * @param arguments Tokenized array of arguments.
     * @return map of arguments to Options that consume them.
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    /* default */ Map<String, ArgumentIndex> getArgumentMap(@NonNull final String... arguments) {
        final Map<String, ArgumentIndex> argumentMap = new ConcurrentHashMap<>();

        for (int i = 0; i < arguments.length; ++i) {
            final String argument = arguments[i];
            final Option<?> option = options.stream()
                    .filter(opt -> opt.match(argument) > 0)
                    .findAny()
                    .orElse(null);

            if (option == null) {
                continue;
            }

            final int consumeCount = option.match(argument);

            argumentMap.put(option.getLongOption(), new ArgumentIndex(i, consumeCount));
            i += consumeCount;
        }

        return argumentMap;
    }

    /**
     * Get help text.
     *
     * @return help text.
     */
    /* default */ String getHelpMessage() {
        final StringBuilder stringBuilder = new StringBuilder(String.format(
                "%s%n%n%s%n%s%n%n%s%n",
                aboutText,
                USAGE_HEADER,
                usageText,
                OPTIONS_HEADER));

        for (final Option<?> option : options) {
            stringBuilder.append(option.toString()).append(System.getProperty("line.separator"));
        }

        return stringBuilder.toString();
    }

    /**
     * Check if command matches this command.
     *
     * @param command Command entered by user.
     * @return {@code true} if command equals this command's name, otherwise {@code false}.
     */
    public boolean match(@NonNull final String command) {
        return command.equals(this.name);
    }

    /**
     * Parse parameters to a matched option.
     *
     * @param parameters List of parameters to the option.
     * @return Event which can be published to the event bus.
     * @throws IllegalArgumentException if length of parameters is different from expected value.
     */
    /* default */ abstract E parse(@NonNull final String... parameters)
            throws IllegalArgumentException;

    /**
     * Pair of start index and count of arguments consumed by an option.
     */
    @Value
    /* default */ static class ArgumentIndex {
        /** Start index of arguments in tokenized array. */
        private final int startIndex;
        /** Number of arguments consumed by option. */
        private final int argumentCount;
    }
}
