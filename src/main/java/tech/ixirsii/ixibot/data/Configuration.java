package tech.ixirsii.ixibot.data;

import lombok.NonNull;

/**
 * Bot configuration.
 *
 * @author Ryan Porterfield
 * @param commandPrefix If a message starts with this prefix the bot will attempt to parse a command from it.
 * @param discordToken Discord API token.
 * @since 1.0.0
 */
public record Configuration(@NonNull String commandPrefix, @NonNull String discordToken) {
}
