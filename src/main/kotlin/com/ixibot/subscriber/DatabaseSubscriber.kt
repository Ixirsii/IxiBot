package com.ixibot.subscriber

import com.google.common.eventbus.Subscribe
import com.ixibot.Logging
import com.ixibot.LoggingImpl
import com.ixibot.database.Database
import com.ixibot.event.AddRoleReactionEvent
import java.sql.SQLException

/**
 * Subscribe to events which modify the database.
 *
 * @author Ixirsii <ixirsii@ixirsii.tech>
 */
class DatabaseSubscriber(
    /**
     * Database interface.
     */
    private val database: Database,
) : Logging by LoggingImpl<DatabaseSubscriber>() {

    /**
     * RoleReactionEvent subscriber.
     *
     * @param event Event published to event bus.
     */
    @Subscribe
    fun onRoleReactionEvent(event: AddRoleReactionEvent) {
        TODO("Update this with new event")
//        val roleReaction = event.roleReaction
//        try {
//            if (event.isCreate) {
//                database.addRoleReaction(roleReaction)
//            } else {
//                database.deleteRoleReaction(roleReaction)
//            }
//        } catch (sqle: SQLException) {
//            log.error("Failed to process role reaction event {}", event, sqle)
//        }
    }
}
