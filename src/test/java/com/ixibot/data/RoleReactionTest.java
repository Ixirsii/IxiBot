package com.ixibot.data;

import com.ixibot.util.TestData;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RoleReactionTest {
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
