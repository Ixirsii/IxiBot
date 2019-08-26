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

import java.util.Arrays;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Command option which is true if present.
 *
 * @author Ryan Porterfield
 */
@Slf4j
class PresenceOption extends Option<Boolean> {

    /**
     * Default constructor.
     *
     * @param longOption  {@link Option#longOption}
     * @param shortOption {@link Option#shortOption}
     * @param aboutText   {@link Option#aboutText}
     */
    /* default */ PresenceOption(final String longOption,
                                 final char shortOption,
                                 final String aboutText) {
        super(longOption, shortOption, 0, aboutText);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    /* default */ Boolean parse(@NonNull final String... parameters)
            throws IllegalArgumentException {
        if (parameters.length != getParameterCount()) {
            final String errorMessage = String.format(
                    "Incorrect number of arguments passed to option \"%s\". "
                            + "Expected %d but was %d, %s",
                    getLongOption(),
                    getParameterCount(),
                    parameters.length,
                    Arrays.toString(parameters));

            log.debug(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final String option = getShortOptionText() + ", " + getLongOptionText();
        final String optionSpace = getSpace(option.length());

        return option + optionSpace + getAboutText() + ".";
    }
}
