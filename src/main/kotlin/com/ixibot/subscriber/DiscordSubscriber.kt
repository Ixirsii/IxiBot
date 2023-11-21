package com.ixibot.subscriber

import com.google.common.eventbus.Subscribe
import com.ixibot.logging.Logging
import com.ixibot.logging.LoggingImpl
import com.ixibot.data.RoleReaction
import com.ixibot.database.Database
import com.ixibot.event.DiscordReactionEvent
import discord4j.common.util.Snowflake
import discord4j.core.`object`.entity.Member
import discord4j.core.`object`.entity.Message
import discord4j.core.`object`.reaction.ReactionEmoji
import reactor.core.Disposable
import java.util.Optional

/**
 * Subscribe to events which trigger Discord actions.
 *
 * @author Ixirsii <ixirsii@ixirsii.tech>
 */
class DiscordSubscriber(
    /** Database interface. */
    private val database: Database,
) : Logging by LoggingImpl<DiscordSubscriber>() {

    // TODO: Add documentation
    @Throws(IllegalArgumentException::class)
    private fun getFilter(
        channelID: Snowflake,
        messageID: Snowflake,
        optionalCustom: Optional<ReactionEmoji.Custom>,
        optionalUnicode: Optional<ReactionEmoji.Unicode>,
    ): (reaction: RoleReaction) -> Boolean {
        return when {
            optionalCustom.isPresent -> {
                { reaction: RoleReaction ->
                    val custom: ReactionEmoji.Custom = optionalCustom.get()
                    (reaction.messageID == messageID &&
                            reaction.channelID == channelID &&
                            reaction.reactionEmojiName == custom.name &&
                            reaction.boxedReactionEmojiID == custom.id.asLong())
                }
            }

            optionalUnicode.isPresent -> {
                { reaction: RoleReaction ->
                    val unicode = optionalUnicode.get()
                    (reaction.messageID == messageID &&
                            reaction.channelID == channelID &&
                            reaction.reactionEmojiName == unicode.raw)
                }
            }

            else -> {
                throw IllegalArgumentException("One of either optionalCustom or optionalUnicode must be present")
            }
        }
    }

    // TODO: Add documentation
    private fun getMemberConsumer(
        isAdd: Boolean,
        messageID: Long,
        reactionEmoji: ReactionEmoji,
        roleID: Snowflake,
    ): (Member) -> Disposable {

        val reasonFormat: String = getReasonFormat(isAdd)

        return { member: Member ->
            val reason: String = String.format(
                reasonFormat,
                member.displayName,
                messageID,
                reactionEmoji,
                roleID
            )
            log.info(reason)
            if (isAdd) {
                member.addRole(roleID, reason).subscribe()
            } else {
                member.removeRole(roleID, reason).subscribe()
            }
        }
    }

    // TODO: Add documentation
    private fun getReasonFormat(isAdd: Boolean): String {
        return if (isAdd)
            "User %s reacted to message %d with %s to get role %s."
        else
            "User %s reacted to message %d with %s to remove role %s."
    }

    /**
     * DiscordReactionEvent subscriber.
     *
     * @param event Event published to the event bus.
     */
    @Subscribe
    fun onDiscordReactionEvent(event: DiscordReactionEvent) {
        val filter: (reaction: RoleReaction) -> Boolean = try {
            getFilter(
                channelID = event.channelID,
                messageID = event.messageID,
                optionalCustom = event.reactionEmoji.asCustomEmoji(),
                optionalUnicode = event.reactionEmoji.asUnicodeEmoji()
            )
        } catch (iae: IllegalArgumentException) {
            log.error(
                "Failed to get reaction that user added to message.\nUser: {}\nMessage: {}\nEmoji: {}",
                event.userID,
                event.messageID,
                event.reactionEmoji,
                iae
            )
            return
        }

        val reactionOptional: Optional<RoleReaction> = database.allRoleReactions.stream().filter(filter).findFirst()
        val (_, _, _, reactionEmoji, roleID) = reactionOptional.get()

        if (reactionOptional.isPresent) {
            event.messageMono.subscribe { message: Message ->
                message.authorAsMember.subscribe {
                    getMemberConsumer(event.isAdd, message.id.asLong(), reactionEmoji, roleID)
                }
            }
        }
    }
}
