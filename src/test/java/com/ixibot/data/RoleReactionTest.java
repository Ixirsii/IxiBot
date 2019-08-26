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

package com.ixibot.data;

import com.ixibot.util.TestData;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoleReactionTest {
    @Test
    void customGetBoxedReactionEmojiID() {
        assertEquals(TestData.CUSTOM_EMOJI.getId().asLong(),
                TestData.ROLE_REACTION_1.getBoxedReactionEmojiID(),
                "Getting boxed emoji ID from Custom emoji should equal expected");
    }

    @Test
    void unicodeGetBoxedReactionEmojiID() {
        assertNull(TestData.ROLE_REACTION_2.getBoxedReactionEmojiID(),
                "Getting boxed emoji ID from Unicode emoji should be null");
    }

    @Test
    void customGetReactionEmojiName() {
        assertEquals(TestData.CUSTOM_EMOJI.getName(),
                TestData.ROLE_REACTION_1.getReactionEmojiName(),
                "Custom reaction emoji name should equal expected");
    }

    @Test
    void unicodeGetReactionEmojiName() {
        assertEquals(TestData.UNICODE_EMOJI.getRaw(),
                TestData.ROLE_REACTION_2.getReactionEmojiName(),
                "Unicode reaction emoji name should equal expected");
    }

    @Test
    void addVerifiedIsVerified() {
        assertTrue(TestData.ROLE_REACTION_1.isVerified(),
                "Add verified emoji should be verified");
    }

    @Test
    void removeVerifiedIsVerified() {
        assertTrue(TestData.ROLE_REACTION_2.isVerified(),
                "Add verified emoji should be verified");
    }

    @Test
    void unverifiedIsNotVerified() {
        assertFalse(TestData.ROLE_REACTION_3.isVerified(),
                "Unverified emoji should not be verified");
    }
}
