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

package com.ixibot.database

/**
 * Add verified key.
 */
const val ADD_VERIFIED = "add_verified"

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
 * Remove verified key.
 */
const val REMOVE_VERIFIED = "remove_verified"

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
        "CREATE TABLE IF NOT EXISTS %s (%s INTEGER NOT NULL, %s INTEGER NOT NULL," +
                " %s INTEGER NOT NULL, %s INTEGER, %s INTEGER NOT NULL, %s INTEGER NOT NULL," +
                " %s TEXT NOT NULL, %s INTEGER NOT NULL, %s INTEGER NOT NULL, " +
                "PRIMARY KEY(%s, %s))", /* Table name */
        TABLE_NAME, /* Columns */
        ADD_VERIFIED,
        ANIMATED,
        CHANNEL_ID,
        GUILD_ID,
        MESSAGE_ID,
        REACTION_ID,
        REACTION_NAME,
        REMOVE_VERIFIED,
        ROLE_ID, /* Primary key */
        MESSAGE_ID,
        REACTION_NAME)
