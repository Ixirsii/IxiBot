package com.ixibot.event

import discord4j.common.util.Snowflake
import discord4j.core.`object`.reaction.ReactionEmoji

/**
 * Create role reaction pub/sub event.
 */
class AddRoleReactionEvent(
    /** Channel ID containing the message. */
    val channelID: Snowflake?,
    isHelp: Boolean, isValid: Boolean,
    /** Was the verify flag passed? */
    val isVerify: Boolean,
    /** Was the verify_add flag passed? */
    val isVerifyAdd: Boolean,
    /** Was the verify_remove flag passed? */
    val isVerifyRemove: Boolean,
    /** Message ID containing the reaction. */
    val messageID: Snowflake?,
    /** Reaction emoji name/raw. */
    val reactionEmoji: ReactionEmoji?,
    /** Role ID to (un)assign. */
    val roleID: Snowflake?,
) : CommandEvent<AddRoleReactionEvent, AddRoleReactionEvent.Builder>(isHelp = isHelp, isValid = isValid) {

    /**
     * Get a Builder pre-populated with the values in this event.
     *
     * @return a Builder pre-populated with the values in this event.
     */
    override fun toBuilder(): Builder {
        return Builder()
    }

    /**
     * Builder class for AddRoleReactionEvent.
     */
    data class Builder(
        private var channelID: Snowflake? = null,
        private var isVerify: Boolean = false,
        private var isVerifyAdd: Boolean = false,
        private var isVerifyRemove: Boolean = false,
        private var messageID: Snowflake? = null,
        private var reactionEmoji: ReactionEmoji? = null,
        private var roleID: Snowflake? = null,
    ) : CommandEvent.Builder<AddRoleReactionEvent, Builder>() {

        override fun build(): AddRoleReactionEvent {
            return AddRoleReactionEvent(
                channelID = channelID,
                isHelp = isHelp,
                isValid = isValid,
                isVerify = isVerify,
                isVerifyAdd = isVerifyAdd,
                isVerifyRemove = isVerifyRemove,
                messageID = messageID,
                reactionEmoji = reactionEmoji,
                roleID = roleID
            )
        }

        override fun self(): Builder {
            return this
        }

        fun channelID(channelID: Snowflake): Builder = apply { this.channelID = channelID }

        fun isVerify(verify: Boolean): Builder = apply { this.isVerify = verify }

        fun isVerifyAdd(verifyAdd: Boolean): Builder = apply { this.isVerifyAdd = verifyAdd }

        fun isVerifyRemove(verifyRemove: Boolean): Builder = apply { this.isVerifyRemove = verifyRemove }

        fun messageID(messageID: Snowflake): Builder = apply { this.messageID = messageID }

        fun reactionEmoji(reactionEmoji: ReactionEmoji): Builder = apply { this.reactionEmoji = reactionEmoji }

        fun roleID(roleID: Snowflake): Builder = apply { this.roleID = roleID }
    }
}
