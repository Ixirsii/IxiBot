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

import java.util.List;

import com.google.common.collect.Lists;
import lombok.NonNull;

/**
 * Command base class.
 *
 * @author Ryan Porterfield
 */
public abstract class Command {
    /** Help option about text. */
    private static final String ABOUT_HELP = "Show this help message";
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
            stringBuilder.append(option.toString()).append('\n');
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
     * @throws IllegalArgumentException if length of parameters is different from {@link
     *                                  Option#parameterCount}
     */
    /* default */ abstract void parse(@NonNull final String... parameters)
            throws IllegalArgumentException;
}
