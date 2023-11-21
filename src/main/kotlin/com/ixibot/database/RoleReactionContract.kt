package com.ixibot.database

/**
 * Reaction emoji is animated key.
 */
const val ANIMATED = "animated"

/**
 * Channel ID key.
 */
const val CHANNEL_ID = "channel_id"

/**
 * Guild ID key.
 */
const val GUILD_ID = "guild_id"

/**
 * Message ID key.
 */
const val MESSAGE_ID = "message_id"

/**
 * Reaction emoji ID key.
 */
const val REACTION_ID = "reaction_id"

/**
 * Reaction emoji name key.
 */
const val REACTION_NAME = "reaction_name"

/**
 * Guild role ID key.
 */
const val ROLE_ID = "role_id"

/**
 * Table name.
 */
const val TABLE_NAME = "role_reactions"

/**
 * SQL statement for creating the table.
 */
val CREATE_TABLE = String.format(
    "CREATE TABLE IF NOT EXISTS %s (%s INTEGER NOT NULL," +
            " %s INTEGER NOT NULL, %s INTEGER, %s INTEGER NOT NULL, %s INTEGER NOT NULL," +
            " %s TEXT NOT NULL, %s INTEGER NOT NULL, PRIMARY KEY(%s, %s))",
    /* Table name */
    TABLE_NAME,
    /* Columns */
    ANIMATED,
    CHANNEL_ID,
    GUILD_ID,
    MESSAGE_ID,
    REACTION_ID,
    REACTION_NAME,
    ROLE_ID,
    /* Primary key */
    MESSAGE_ID,
    REACTION_NAME
)
