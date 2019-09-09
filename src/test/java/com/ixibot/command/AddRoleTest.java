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

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AddRoleTest {
    private static final String ARGUMENTS = "--verify #channel 1234567890 \"EZ Clap\" @Member";
    private static final String[] ARGUMENT_TOKENS = new String[] {
            "--verify",
            "#channel",
            "1234567890",
            "EZ Clap",
            "@Member",
    };

    private AddRole underTest = new AddRole();

    @Test
    void argumentTokenizer() {
        assertArrayEquals(
                ARGUMENT_TOKENS,
                underTest.argumentTokenizer(ARGUMENTS),
                "Arguments should get tokenized correctly");
    }

    @Test
    void argumentTokenizerWhenUnterminatedQuote() {
        assertThrows(
                IllegalArgumentException.class,
                () -> underTest.argumentTokenizer("--verify #channel 1234567890 \"EZ Clap @Member"),
                "Arguments should get tokenized correctly");
    }

    @Test
    void getArgumentMap() {
        assertEquals(
                ImmutableMap.of("--verify", new Command.ArgumentIndex(0, 1)),
                underTest.getArgumentMap(ARGUMENT_TOKENS),
                "argument map should equal expected");
    }

    @Test
    void getHelpMessage() {
        assertEquals(
                String.format(
                        "%s%n%n%s%n%s%n%n%s%n%s%n%s%n%s%n%s%n",
                        "Add a role reaction listener",
                        "Usage:",
                        "addrole [options] <channel> <message id> <emoji> <role>",
                        "Options:",
                        "-h, --help              Show this help message.",
                        "-V, --verify            Run both add and remove verify checks on this role reaction.",
                        "-A, --verifyadd         Run add verify checks on this role reaction.",
                        "-R, --verifyremove      Run remove verify checks on this role reaction."
                ),
                underTest.getHelpMessage(),
                "help message should equal expected");
    }

    @Test
    void matchWhenMatches() {
        assertTrue(underTest.match("addrole"), "Match should be true when command matches");
    }

    @Test
    void matchWhenDoesNotMatch() {
        assertFalse(
                underTest.match("notacommand"),
                "Match should be false when command does not match");
    }
}