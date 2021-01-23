/*
 * Copyright (c) 2021, Ryan Porterfield
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *     1. Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *
 *     3. Neither the name of the copyright holder nor the names of its
 *        contributors may be used to endorse or promote products derived from
 *        this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 *  IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 *  TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 *  PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 *  HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 *  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 *  TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.ixibot.commands

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

private const val ARGUMENTS = "--verify #channel 1234567890 \"EZ Clap\" @Member"
private val ARGUMENT_TOKENS = arrayOf(
        "--verify",
        "#channel",
        "1234567890",
        "EZ Clap",
        "@Member")

class AddRoleCommandTest {
    private val underTest = AddRoleCommand()

    @Test
    fun `GIVEN valid arguments WHEN argumentTokenizer THEN returns tokens`() {
        Assertions.assertArrayEquals(
                ARGUMENT_TOKENS,
                underTest.argumentTokenizer(ARGUMENTS),
                "Arguments should get tokenized correctly")
    }

    @Test
    fun `GIVEN unterminated string WHEN argumentTokenizer THEN throws IllegalArgumentException`() {
        Assertions.assertThrows(
                IllegalArgumentException::class.java,
                { underTest.argumentTokenizer("--verify #channel 1234567890 \"EZ Clap @Member") },
                "Arguments should get tokenized correctly")
    }

    @Test
    fun `GIVEN NA WHEN getHelpMessage THEN message is properly formatted`() {
        Assertions.assertEquals(
                String.format(
                        "%s%n%n%s%n%s%n%n%s%n%s%n%s%n%s%n%s%n",
                        "Add a role reaction listener",
                        "Usage:",
                        "add_role [options] <channel> <message id> <emoji> <role>",
                        "Options:",
                        "-h, --help              Show this help message.",
                        "-V, --verify            Run both add and remove verify checks on this role reaction.",
                        "-A, --verify_add        Run add verify checks on this role reaction.",
                        "-R, --verify_remove     Run remove verify checks on this role reaction."),
                underTest.helpMessage,
                "help message should equal expected")
    }

    @Test
    fun `GIVEN matching command WHEN match THEN returns true`() {
        Assertions.assertTrue(
                underTest.match("add_role"),
                "Match should be true when command matches")
    }

    @Test
    fun `GIVEN non-matching command WHEN match THEN returns false`() {
        Assertions.assertFalse(
                underTest.match("notacommand"),
                "Match should be false when command does not match")
    }
}
