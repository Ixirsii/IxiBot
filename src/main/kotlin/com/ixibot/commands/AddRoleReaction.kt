package com.ixibot.commands

import com.ixibot.command.Command
import com.ixibot.command.OptionalArgument
import com.ixibot.command.booleanParser
import com.ixibot.event.AddRoleReactionEvent

/** Add verify option about text.  */
private const val ABOUT_VERIFY_ADD = "Run add verify checks on this role reaction"

/** Remove verify option about text.  */
private const val ABOUT_VERIFY_RM = "Run remove verify checks on this role reaction"

/** Command name.  */
private const val COMMAND = "add_role"

/** Command help text.  */
private const val ABOUT = "Add a role reaction listener"

/** Command format.  */
private const val USAGE = "$COMMAND [options] <channel> <message id> <emoji> <role>"

/** Verify (both verifyAdd and verifyRemove) option.  */
private val VERIFY = OptionalArgument(
    aboutText = "Run both add and remove verify checks on this role reaction",
    accumulate = { accumulator: AddRoleReactionEvent.Builder, value: Boolean -> accumulator.isVerify(value) },
    longOption = "verify",
    parser = ::booleanParser,
    shortOption = 'V'
)

/** Verify add option.  */
private val VERIFY_ADD = OptionalArgument(
    aboutText = ABOUT_VERIFY_ADD,
    accumulate = { accumulator: AddRoleReactionEvent.Builder, value: Boolean -> accumulator.isVerifyAdd(value) },
    longOption = "verify_add",
    parser = ::booleanParser,
    shortOption = 'A'
)

/** Verify remove option.  */
private val VERIFY_REMOVE = OptionalArgument(
    aboutText = ABOUT_VERIFY_RM,
    accumulate = { accumulator: AddRoleReactionEvent.Builder, value: Boolean -> accumulator.isVerifyRemove(value) },
    longOption = "verify_remove",
    parser = ::booleanParser,
    shortOption = 'R'
)

/**
 * List of options accepted by this command.
 *
 * @see Command.options
 */
private val OPTIONS = listOf<OptionalArgument<Any, AddRoleReactionEvent, AddRoleReactionEvent.Builder>>(
    VERIFY, VERIFY_ADD, VERIFY_REMOVE
)

/**
 * Add a role reaction.
 *
 * @author Ixirsii <ixirsii@ixirsii.tech>
 */
class AddRoleReaction : Command<AddRoleReactionEvent, AddRoleReactionEvent.Builder>(
    aboutText = ABOUT,
    name = COMMAND,
    usageText = USAGE,
    options = OPTIONS
) {

    override fun getAccumulator(): AddRoleReactionEvent.Builder {
        return AddRoleReactionEvent.Builder()
    }
}
