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
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import testUtil.ABOUT_OPTION
import testUtil.LONG_OPTION
import testUtil.SHORT_OPTION

class PresenceOptionTest {
    private val underTest = PresenceOption(LONG_OPTION, SHORT_OPTION, ABOUT_OPTION)

    @Test
    fun getShortOptionText() {
        assertEquals(
                "-$SHORT_OPTION",
                underTest.short,
                "Short option text should equal expected")
    }

    @Test
    fun matchWhenLongOptionDoesNotMatch() {
        assertEquals(
                0,
                underTest.match("--not-an-option"),
                "match should return 0 when long option does not match")
    }

    @Test
    fun matchWhenLongOptionMatches() {
        assertEquals(
                1,
                underTest.match("--$LONG_OPTION"),
                "match should return 1 when long option matches")
    }

    @Test
    fun matchWhenShortOptionDoesNotMatch() {
        assertEquals(
                0,
                underTest.match("-a"),
                "match should return 0 when long option does not match")
    }

    @Test
    fun matchWhenShortOptionDoesNotContainMatch() {
        assertEquals(
                0,
                underTest.match("-abcde"),
                "match should return 0 when long option does not match")
    }

    @Test
    fun matchWhenShortOptionMatches() {
        assertEquals(
                1,
                underTest.match("-$SHORT_OPTION"),
                "match should return 1 when long option matches")
    }

    @Test
    fun matchWhenShortOptionContainsMatch() {
        assertEquals(
                1,
                underTest.match("-abcde$SHORT_OPTION"),
                "match should return 1 when long option contains match")
    }

    @Test
    fun matchWhenPositionalOptionDoesNotMatch() {
        assertEquals(
                0,
                underTest.match("not-an-option"),
                "match should return 0 when positional option does not match")
    }

    @Test
    fun matchWhenPositionalOptionMatches() {
        assertEquals(
                1,
                underTest.match("--$LONG_OPTION"),
                "match should return 1 when positional option matches")
    }

    @Test
    fun parseWhenCorrectNumberOfParameters() {
        assertTrue(
                underTest.parse(),
                "parse should return true when no arguments are passed")
    }

    @Test
    fun parseWhenIncorrectNumberOfParameters() {
        val parameter = "parameter"
        val errorMessage = String.format(
                "Incorrect number of arguments passed to option"
                        + " \"%s\". Expected %d but was %d, [%s]",
                "--$LONG_OPTION",
                0,
                1,
                parameter)
        val exception: Throwable = assertThrows(
                IllegalArgumentException::class.java,
                { underTest.parse(parameter) },
                "parse should throw IllegalArgumentException when arguments are passed")
        assertEquals(
                errorMessage,
                exception.message,
                "exception message should equal expected")
    }

    @Test
    fun toStringTest() {
        assertEquals(
                "-s, --long-option       This is an option used for testing.",
                underTest.toString(),
                "toString should equal expected")
    }
}