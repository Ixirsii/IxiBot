/*
 * Copyright (c) 2020, Ryan Porterfield
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

package com.ixibot.data

import discord4j.common.util.Snowflake
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import testUtil.CUSTOM_EMOJI
import testUtil.ROLE_REACTION_1
import testUtil.ROLE_REACTION_2
import testUtil.ROLE_REACTION_3
import testUtil.UNICODE_EMOJI

class RoleReactionTest {
    private val snowflake: Snowflake = Snowflake.of(1L)
    @Test
    fun `GIVEN channelID WHEN channelID THEN returns channelID`() {
        assertEquals(snowflake, ROLE_REACTION_1.channelID, "chanelID should equal expected")
    }

    @Test
    fun `GIVEN guildID WHEN guildID THEN returns guildID`() {
        assertEquals(snowflake, ROLE_REACTION_1.guildID, "guildID should equal expected")
    }

    @Test
    fun `GIVEN isAddVerified WHEN isAddVerified THEN returns true`() {
        assertTrue(ROLE_REACTION_1.isAddVerified, "isAddVerified should be true")
    }

    @Test
    fun `GIVEN not isAddVerified WHEN isAddVerified THEN returns false`() {
        assertFalse(ROLE_REACTION_2.isAddVerified, "isAddVerified should be false")
    }

    @Test
    fun `GIVEN isRemoveVerified WHEN isRemoveVerified THEN returns true`() {
        assertTrue(ROLE_REACTION_2.isRemoveVerified, "isRemoveVerified should be true")
    }

    @Test
    fun `GIVEN not isRemoveVerified WHEN isRemoveVerified THEN returns false`() {
        assertFalse(ROLE_REACTION_1.isRemoveVerified, "isRemoveVerified should be false")
    }

    @Test
    fun `GIVEN messageID WHEN messageID THEN returns messageID`() {
        assertEquals(snowflake, ROLE_REACTION_1.messageID, "messageID should equal expected")
    }

    @Test
    fun `GIVEN reactionEmoji WHEN reactionEmoji THEN returns reactionEmoji`() {
        assertEquals(CUSTOM_EMOJI, ROLE_REACTION_1.reactionEmoji, "reactionEmoji should equal expected")
    }

    @Test
    fun `GIVEN roleID WHEN roleID THEN returns roleID`() {
        assertEquals(snowflake, ROLE_REACTION_1.roleID, "roleID should equal expected")
    }

    @Test
    fun `GIVEN custom emoji WHEN boxedReactionEmojiID THEN returns emoji ID`() {
        assertEquals(
                CUSTOM_EMOJI.id.asLong(),
                ROLE_REACTION_1.boxedReactionEmojiID,
                "Getting boxed emoji ID from Custom emoji should equal expected")
    }

    @Test
    fun `GIVEN unicode emoji WHEN boxedReactionEmojiID THEN returns null`() {
        assertNull(
                ROLE_REACTION_2.boxedReactionEmojiID,
                "Getting boxed emoji ID from Unicode emoji should be null")
    }

    @Test
    fun `GIVEN custom emoji WHEN reactionEmojiName THEN returns custom name`() {
        assertEquals(
                CUSTOM_EMOJI.name,
                ROLE_REACTION_1.reactionEmojiName,
                "Custom reaction emoji name should equal expected")
    }

    @Test
    fun `GIVEN unicode emoji WHEN reactionEmojiName THEN returns unicode raw`() {
        assertEquals(
                UNICODE_EMOJI.raw,
                ROLE_REACTION_2.reactionEmojiName,
                "Unicode reaction emoji name should equal expected")
    }

    @Test
    fun `GIVEN addVerified WHEN isVerified THEN returns true`() {
        assertTrue(
                ROLE_REACTION_1.isVerified,
                "Add verified reaction should be verified")
    }

    @Test
    fun `GIVEN removedVerified WHEN isVerified THEN returns true`() {
        assertTrue(
                ROLE_REACTION_2.isVerified,
                "Remove verified reaction should be verified")
    }

    @Test
    fun `GIVEN not verified WHEN isVerified THEN returns false`() {
        assertFalse(
                ROLE_REACTION_3.isVerified,
                "Unverified reaction should not be verified")
    }
}
