/*
 * Copyright (c) 2020, Ryan Porterfield
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *     1. Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *
 *     3. Neither the name of the copyright holder nor the names of its
 *        contributors may be used to endorse or promote products derived from
 *        this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 *  IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 *  TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 *  PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 *  HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 *  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 *  TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.ixibot.subscriber

import com.ixibot.database.Database
import com.ixibot.event.RoleReactionEvent
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import testUtil.ROLE_REACTION_1
import java.sql.SQLException

@ExtendWith(MockKExtension::class)
class DatabaseSubscriberTest {
    @RelaxedMockK
    private lateinit var databaseMock: Database

    @InjectMockKs
    private lateinit var underTest: DatabaseSubscriber

    @AfterEach
    fun cleanUp() {
        confirmVerified(databaseMock)
    }

    @Test
    fun `GIVEN create event WHEN onRoleReaction THEN adds to database`() {
        val roleReactionEvent = RoleReactionEvent(isCreate = true, roleReaction = ROLE_REACTION_1)

        underTest.onRoleReactionEvent(roleReactionEvent)

        verify { databaseMock.addRoleReaction(ROLE_REACTION_1) }
    }

    @Test
    fun `GIVEN delete event WHEN onRoleReaction THEN deletes from database`() {
        val roleReactionEvent = RoleReactionEvent(isCreate = false, roleReaction = ROLE_REACTION_1)

        underTest.onRoleReactionEvent(roleReactionEvent)

        verify { databaseMock.deleteRoleReaction(ROLE_REACTION_1) }
    }

    @Test
    fun `GIVEN SQLException WHEN onRoleReaction THEN does nothing`() {
        val roleReactionEvent = RoleReactionEvent(isCreate = true, roleReaction = ROLE_REACTION_1)

        every { databaseMock.addRoleReaction(ROLE_REACTION_1) } throws SQLException()

        underTest.onRoleReactionEvent(roleReactionEvent)

        verify { databaseMock.addRoleReaction(ROLE_REACTION_1) }
    }
}