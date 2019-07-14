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

package com.ixibot.database;

/**
 * Database contract for role assignment reactions.
 *
 * @author Ryan Porterfield
 */
final class RoleReactionContract {
    /**
     * Add verified key.
     */
    static final String ADD_VERIFIED = "add_verified";
    /**
     * Reaction emoji is animated key.
     */
    static final String ANIMATED = "animated";
    /**
     * Channel ID key.
     */
    static final String CHANNEL_ID = "channel_id";
    /**
     * Guild ID key.
     */
    static final String GUILD_ID = "guild_id";
    /**
     * Message ID key.
     */
    static final String MESSAGE_ID = "message_id";
    /**
     * Reaction emoji ID key.
     */
    static final String REACTION_ID = "reaction_id";
    /**
     * Reaction emoji name key.
     */
    static final String REACTION_NAME = "reaction_name";
    /**
     * Remove verified key.
     */
    static final String REMOVE_VERIFIED = "remove_verified";
    /**
     * Guild role ID key.
     */
    static final String ROLE_ID = "role_id";
    /**
     * Table name.
     */
    static final String TABLE_NAME = "role_reactions";

    /**
     * SQL statement for creating the table.
     */
    static final String CREATE_TABLE_STATEMENT = String.format(
            "CREATE TABLE IF NOT EXISTS %s (%s INTEGER NOT NULL, %s INTEGER NOT NULL,"
                    + " %s INTEGER NOT NULL, %s INTEGER, %s INTEGER NOT NULL, %s INTEGER NOT NULL,"
                    + " %s TEXT NOT NULL, %s INTEGER NOT NULL, %s INTEGER NOT NULL, "
                    + "PRIMARY KEY(%s, %s))",
            /* Table name */
            TABLE_NAME,
            /* Columns */
            ADD_VERIFIED,
            ANIMATED,
            CHANNEL_ID,
            GUILD_ID,
            MESSAGE_ID,
            REACTION_ID,
            REACTION_NAME,
            REMOVE_VERIFIED,
            ROLE_ID,
            /* Primary key */
            MESSAGE_ID,
            REACTION_NAME
    );

    /**
     * Hide utility class constructor.
     */
    private RoleReactionContract() {
    }
}
