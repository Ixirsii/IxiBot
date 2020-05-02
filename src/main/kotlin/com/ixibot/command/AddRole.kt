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

package com.ixibot.command

import com.ixibot.event.RoleReactionEvent

/** Verify option about text.  */
private const val ABOUT_VERIFY = ("Run both add and remove verify checks on this role reaction")
/** Add verify option about text.  */
private const val ABOUT_VERIFY_ADD = "Run add verify checks on this role reaction"
/** Remove verify option about text.  */
private const val ABOUT_VERIFY_RM = ("Run remove verify checks on this role reaction")
/** Command name.  */
private const val COMMAND = "add_role"
/** Command help text.  */
private const val ABOUT = "Add a role reaction listener"
/** Command format.  */
private const val USAGE = "$COMMAND [options] <channel> <message id> <emoji> <role>"
/** Verify (both verifyAdd and verifyRemove) option.  */
private val VERIFY = PresenceOption(
        "verify",
        'V',
        ABOUT_VERIFY)
/** Verify add option.  */
private val VERIFY_ADD = PresenceOption(
        "verify_add",
        'A',
        ABOUT_VERIFY_ADD)
/** Verify remove option.  */
private val VERIFY_REMOVE = PresenceOption(
        "verify_remove",
        'R',
        ABOUT_VERIFY_RM)
/**
 * List of options accepted by this command.
 *
 * @see Command.options
 */
private val OPTIONS = arrayOf<Option<out Any?>?>(
        VERIFY,
        VERIFY_ADD,
        VERIFY_REMOVE)

/**
 * Add a role reaction.
 *
 * @author Ryan Porterfield
 */
class AddRole : Command<RoleReactionEvent?>(COMMAND, OPTIONS, ABOUT, USAGE) {

    @Throws(IllegalArgumentException::class)
    override fun parse(vararg parameters: String): RoleReactionEvent? {
        // TODO: Implement this later
        return null
    }
}