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

package com.ixibot.command

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import testUtil.ABOUT_OPTION
import testUtil.LONG_OPTION
import testUtil.SHORT_OPTION
import testUtil.TestCommandEvent

class BooleanOptionTest {
    private val underTest = BooleanOption(
        aboutText = ABOUT_OPTION,
        accumulate = { accumulator: TestCommandEvent.TestCommandEventBuilder, value: Boolean -> accumulator.testValue(value) },
        longOption = LONG_OPTION,
        shortOption = SHORT_OPTION
    )

    @Test
    fun `GIVEN different long option WHEN match THEN returns false`() {
        assertFalse(
            underTest.match("--not-an-option"),
            "match should return false when long option does not match"
        )
    }

    @Test
    fun `GIVEN long option WHEN match THEN returns true`() {
        assertTrue(
            underTest.match("--$LONG_OPTION"),
            "match should return true when long option matches"
        )
    }

    @Test
    fun `GIVEN different short option WHEN match THEN returns false`() {
        assertFalse(
            underTest.match("-a"),
            "match should return false when short option does not match"
        )
    }

    @Test
    fun `GIVEN absent short option WHEN match THEN returns false`() {
        assertFalse(
            underTest.match("-abcde"),
            "match should return false when short option is not present"
        )
    }

    @Test
    fun `GIVEN short option WHEN match THEN returns true`() {
        assertTrue(
            underTest.match("-$SHORT_OPTION"),
            "match should return true when short option matches"
        )
    }

    @Test
    fun `GIVEN present short option WHEN match THEN returns true`() {
        assertTrue(
            underTest.match("-abcde$SHORT_OPTION"),
            "match should return true when short option is present"
        )
    }

    @Test
    fun `GIVEN NA WHEN parse THEN returns true`() {
        assertTrue(
            underTest.parseArgs("", emptyList()),
            "parse should return true"
        )
    }

    @Test
    fun `GIVEN NA WHEN toString THEN result is correctly formatted`() {
        assertEquals(
            "-$SHORT_OPTION, --$LONG_OPTION       $ABOUT_OPTION.",
            underTest.toString(),
            "toString should equal expected"
        )
    }
}
