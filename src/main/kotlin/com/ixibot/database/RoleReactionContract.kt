package com.ixibot.database

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
 * Reaction emoji name key.
 */
const val NAME = "name"

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
const val CREATE_TABLE = """
    CREATE TABLE IF NOT EXISTS $TABLE_NAME (
        $CHANNEL_ID INTEGER NOT NULL, $GUILD_ID INTEGER NOT NULL, $MESSAGE_ID INTEGER NOT NULL,
        $NAME TEXT NOT NULL, $ROLE_ID INTEGER NOT NULL, PRIMARY KEY($MESSAGE_ID, $NAME))
"""
