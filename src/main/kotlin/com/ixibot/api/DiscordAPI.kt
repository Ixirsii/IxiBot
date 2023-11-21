package com.ixibot.api

import com.ixibot.logging.Logging
import com.ixibot.logging.LoggingImpl
import com.ixibot.data.RoleReaction
import discord4j.common.util.Snowflake
import discord4j.core.GatewayDiscordClient
import discord4j.core.`object`.entity.Guild
import discord4j.core.`object`.entity.Member
import discord4j.core.`object`.entity.Message
import discord4j.core.`object`.entity.User
import reactor.core.publisher.Mono

/**
 * Discord4J wrapper.
 *
 * @author Ixirsii <ixirsii@ixirsii.tech>
 */
class DiscordAPI(
    /** Discord client. */
    private val discordClient: GatewayDiscordClient,
) : Logging by LoggingImpl<DiscordAPI>() {

}
