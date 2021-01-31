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

package com.ixibot.command

import com.ixibot.commands.AddRoleReaction
import com.ixibot.event.AddRoleReactionEvent
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.lang.IllegalArgumentException

private const val COMMAND_PREFIX: String = "."
// TODO: Add tests for multiple short options
private const val ALL_SHORT_OPTIONS: String = "add_roll -hVAR"

class CommandRepositoryTest {
    private lateinit var underTest: CommandRepository

    @BeforeEach
    fun setup() {
        underTest = CommandRepository(commandPrefix = COMMAND_PREFIX)
    }

    @Test
    fun `GIVEN valid command WHEN isCommand THEN returns true`() {
        Assertions.assertTrue(underTest.isCommand(".add_role"),
            "String starting with command prefix should be a command")
    }

    @Test
    fun `GIVEN invalid command WHEN isCommand THEN returns false`() {
        Assertions.assertFalse(underTest.isCommand("add_role"),
            "String not starting with command prefix should not be a command")
    }

    @Test
    fun `GIVEN registered command WHEN parse THEN returns event`() {
        // Given
        val addRole: Command<AddRoleReactionEvent> = AddRoleReaction()

        underTest.register(addRole)

        // When
        val actual = underTest.parse(".add_role --verify #channel 1234567890 \"EZ Clap\" @Member")

        // Then
        // TODO: Add more assertions for returned event
        Assertions.assertTrue(actual is AddRoleReactionEvent, "Result should be of expected type")
    }

    @Test
    fun `GIVEN unregistered command WHEN parse THEN throws IAE`() {
        Assertions.assertThrows(
            IllegalArgumentException::class.java,
            { underTest.parse(".add_role") },
            "Should throw IllegalArgumentException when parsing unregistered command")
    }

    @Test
    fun `GIVEN registered command WHEN unregister Command THEN returns true`() {
        // Given
        val addRole: Command<AddRoleReactionEvent> = AddRoleReaction()
        underTest.register(addRole)

        // When
        val actual: Boolean = underTest.unregister(addRole)

        // Then
        Assertions.assertTrue(actual, "Should unregister command successfully")
    }

    @Test
    fun `GIVEN registered command WHEN unregister String THEN returns true`() {
        // Given
        val addRole: Command<AddRoleReactionEvent> = AddRoleReaction()
        underTest.register(addRole)

        // When
        val actual: Boolean = underTest.unregister(addRole.name)

        // Then
        Assertions.assertTrue(actual, "Should unregister command successfully")
    }

    @Test
    fun `GIVEN unregistered command WHEN unregister Command THEN returns false`() {
        // Given
        val addRole: Command<AddRoleReactionEvent> = AddRoleReaction()

        // When
        val actual: Boolean = underTest.unregister(addRole)

        // Then
        Assertions.assertFalse(actual, "Should not unregister command successfully")
    }

    @Test
    fun `GIVEN unregistered command WHEN unregister String THEN returns false`() {
        // Given
        val addRole: Command<AddRoleReactionEvent> = AddRoleReaction()

        // When
        val actual: Boolean = underTest.unregister(addRole.name)

        // Then
        Assertions.assertFalse(actual, "Should not unregister command successfully")
    }
}
