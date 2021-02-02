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

import com.ixibot.event.AddRoleReactionEvent
import discord4j.core.`object`.reaction.ReactionEmoji
import discord4j.core.`object`.util.Snowflake
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

private const val CHANNEL_ID_STRING: String = "1"
private const val EMOJI_STRING: String = ":smile:"
private const val MESSAGE_ID_STRING: String = "2"
private const val ROLE_ID_STRING: String = "3"
// TODO: Add tests for these
private val CHANNEL_ID: Snowflake = Snowflake.of(CHANNEL_ID_STRING)
private val MESSAGE_ID: Snowflake = Snowflake.of(MESSAGE_ID_STRING)
private val REACTION_EMOJI: ReactionEmoji = ReactionEmoji.unicode(EMOJI_STRING)
private val ROLE_ID: Snowflake = Snowflake.of(ROLE_ID_STRING)

class AddRoleReactionTest {
    private val underTest = AddRoleReaction()

    @Test
    fun `GIVEN NA WHEN getHelpMessage THEN message is properly formatted`() {
        val expected: String = String.format(
            "%s%n%n%s%n%s%n%n%s%n%s%n%s%n%s%n%s%n",
            "Add a role reaction listener",
            "Usage:",
            "add_role [options] <channel> <message id> <emoji> <role>",
            "Options:",
            "-h, --help              Show this help message.",
            "-V, --verify            Run both add and remove verify checks on this role reaction.",
            "-A, --verify_add        Run add verify checks on this role reaction.",
            "-R, --verify_remove     Run remove verify checks on this role reaction."
        )

        assertEquals(expected, underTest.helpMessage, "help message should equal expected")
    }

    @Test
    fun `GIVEN matching command WHEN match THEN returns true`() {
        assertTrue(
                underTest.match("add_role"),
                "Match should be true when command matches")
    }

    @Test
    fun `GIVEN non-matching command WHEN match THEN returns false`() {
        assertFalse(
                underTest.match("not_a_command"),
                "Match should be false when command does not match")
    }

//    @Test
    fun `GIVEN invalid input WHEN parse THEN isValid is false`() {
        // Given
        val arguments: List<String> = listOf(CHANNEL_ID_STRING, MESSAGE_ID_STRING, EMOJI_STRING)

        // When
        val actual: AddRoleReactionEvent = underTest.parse(arguments)

        // Then
        assertFalse(actual.isHelp, "isHelp should be false when not present in command")
        // TODO: Uncomment this once validation is complete
//        assertFalse(actual.isValid, "isValid should be false when command is not properly formatted")
        assertFalse(actual.isVerify, "isVerify should be false when not present in command")
        assertFalse(actual.isVerifyAdd, "isVerifyAdd should be false when not present in command")
        assertFalse(actual.isVerifyRemove, "isVerifyRemove should be false when not present in command")
    }

//    @Test
    fun `GIVEN valid input WHEN parse THEN isValid is true`() {
        // Given
        val arguments: List<String> = listOf(CHANNEL_ID_STRING, MESSAGE_ID_STRING, EMOJI_STRING, ROLE_ID_STRING)

        // When
        val actual: AddRoleReactionEvent = underTest.parse(arguments)

        // Then
        assertFalse(actual.isHelp, "isHelp should be false when not present in command")
        // TODO: Uncomment this once validation logic is complete
        //assertTrue(actual.isValid, "isValid should be true when command is properly formatted")
        assertFalse(actual.isVerify, "isVerify should be false when not present in command")
        assertFalse(actual.isVerifyAdd, "isVerifyAdd should be false when not present in command")
        assertFalse(actual.isVerifyRemove, "isVerifyRemove should be false when not present in command")
    }

    @Test
    fun `GIVEN help long option WHEN parse THEN isHelp is true`() {
        // Given
        val arguments: List<String> = listOf("--help")

        // When
        val actual: AddRoleReactionEvent = underTest.parse(arguments)

        // Then
        assertTrue(actual.isHelp, "isHelp should be true when passed as long option")
    }

    @Test
    fun `GIVEN help short option WHEN parse THEN isHelp is true`() {
        // Given
        val arguments: List<String> = listOf("-h")

        // When
        val actual: AddRoleReactionEvent = underTest.parse(arguments)

        // Then
        assertTrue(actual.isHelp, "isHelp should be true when passed as short option")
    }

    @Test
    fun `GIVEN verify long option WHEN parse THEN verify is true`() {
        // Given
        val arguments: List<String> = listOf("--verify")

        // When
        val actual: AddRoleReactionEvent = underTest.parse(arguments)

        // Then
        assertTrue(actual.isVerify, "isVerify should be true when passed as long option")
    }

    @Test
    fun `GIVEN verify short option WHEN parse THEN verify is true`() {
        // Given
        val arguments: List<String> = listOf("-V")

        // When
        val actual: AddRoleReactionEvent = underTest.parse(arguments)

        // Then
        assertTrue(actual.isVerify, "isVerify should be true when passed as short option")
    }

    @Test
    fun `GIVEN verifyAdd long option WHEN parse THEN verify is true`() {
        // Given
        val arguments: List<String> = listOf("--verify_add")

        // When
        val actual: AddRoleReactionEvent = underTest.parse(arguments)

        // Then
        assertTrue(actual.isVerifyAdd, "isVerifyAdd should be true when passed as long option")
    }

    @Test
    fun `GIVEN verifyAdd short option WHEN parse THEN verify is true`() {
        // Given
        val arguments: List<String> = listOf("-A")

        // When
        val actual: AddRoleReactionEvent = underTest.parse(arguments)

        // Then
        assertTrue(actual.isVerifyAdd, "isVerifyAdd should be true when passed as short option")
    }

    @Test
    fun `GIVEN verifyRemove long option WHEN parse THEN verify is true`() {
        // Given
        val arguments: List<String> = listOf("--verify_remove")

        // When
        val actual: AddRoleReactionEvent = underTest.parse(arguments)

        // Then
        assertTrue(actual.isVerifyRemove, "isVerifyRemove should be true when passed as long option")
    }

    @Test
    fun `GIVEN verifyRemove short option WHEN parse THEN verify is true`() {
        // Given
        val arguments: List<String> = listOf("-R")

        // When
        val actual: AddRoleReactionEvent = underTest.parse(arguments)

        // Then
        assertTrue(actual.isVerifyRemove, "isVerifyRemove should be true when passed as short option")
    }
}
