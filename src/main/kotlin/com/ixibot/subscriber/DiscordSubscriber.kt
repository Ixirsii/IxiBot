package com.ixibot.subscriber

import arrow.core.Either
import arrow.core.Option
import com.google.common.eventbus.Subscribe
import com.ixibot.logging.Logging
import com.ixibot.logging.LoggingImpl
import com.ixibot.data.RoleReaction
import com.ixibot.database.Database
import com.ixibot.event.DiscordReactionEvent
import com.ixibot.exception.DatabaseException
import discord4j.common.util.Snowflake
import discord4j.core.`object`.entity.Member
import discord4j.core.`object`.entity.Message

/**
 * Subscribe to events which trigger Discord actions.
 *
 * @author Ixirsii <ixirsii@ixirsii.tech>
 */
class DiscordSubscriber(
    /** Database interface. */
    private val database: Database,
) : Logging by LoggingImpl<DiscordSubscriber>() {

    /**
     * DiscordReactionEvent subscriber.
     *
     * @param event Event published to the event bus.
     */
    @Subscribe
    fun onDiscordReactionEvent(event: DiscordReactionEvent) {
        val result: Either<DatabaseException, Option<RoleReaction>> =
            database.getRoleReaction(event.messageID, event.name)

        result.onRight { option: Option<RoleReaction> ->
            option.onSome { roleReaction: RoleReaction ->
                event.messageMono.subscribe { message: Message ->
                    message.authorAsMember.subscribe { member: Member ->
                        if (event.isAdd) {
                            onAdd(member, event.messageID, event.name, roleReaction.roleID)
                        } else {
                            onRemove(member, event.messageID, event.name, roleReaction.roleID)
                        }
                    }
                }
            }
        }
            .onLeft { log.error("Exception getting role reaction from database", it) }
    }

    /* **************************************** Private utility methods ***************************************** */

    private fun onAdd(member: Member, messageID: Snowflake, name: String, roleID: Snowflake) {
        log.info("User {} reacted to message {} with {} to get role {}.", member.displayName, messageID, name, roleID)

        val reason: String = String.format(
            "User %s reacted to message %d with %s to add role %s.",
            member.displayName,
            messageID,
            name,
            roleID
        )

        member.addRole(roleID, reason).subscribe()
    }

    private fun onRemove(member: Member, messageID: Snowflake, name: String, roleID: Snowflake) {
        log.info("User {} reacted to message {} with {} to remove role {}.", member.displayName, messageID, name, roleID)

        val reason: String = String.format(
            "User %s reacted to message %d with %s to remove role %s.",
            member.displayName,
            messageID,
            name,
            roleID
        )

        member.removeRole(roleID, reason).subscribe()
    }
}
