package com.ixibot.command;

import org.junit.jupiter.api.Test;

import static com.ixibot.util.TestData.ABOUT_OPTION;
import static com.ixibot.util.TestData.LONG_OPTION;
import static com.ixibot.util.TestData.SHORT_OPTION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PresenceOptionTest {
    private PresenceOption underTest = new PresenceOption(LONG_OPTION, SHORT_OPTION, ABOUT_OPTION);

    @Test
    void getLongOptionText() {
        assertEquals(
                "--" + LONG_OPTION,
                underTest.getLongOptionText(),
                "Long option text should equal expected");
    }

    @Test
    void getShortOptionText() {
        assertEquals(
                "-" + SHORT_OPTION,
                underTest.getShortOptionText(),
                "Short option text should equal expected");
    }

    @Test
    void getEmptySpaceWhenLengthEqualsColumnLength() {
        assertEquals(
                " ",
                underTest.getSpace(24),
                "Space should equal expected");
    }

    @Test
    void getEmptySpaceWhenLengthGreaterThanColumnLength() {
        assertEquals(
                " ",
                underTest.getSpace(25),
                "Space should equal expected");
    }

    @Test
    void getSpace() {
        assertEquals(
                "              ",
                underTest.getSpace(10),
                "Space should equal expected");
    }

    @Test
    void matchWhenLongOptionDoesNotMatch() {
        assertEquals(
                -1,
                underTest.match("--not-an-option"),
                "match should return -1 when long option does not match");
    }

    @Test
    void matchWhenLongOptionMatches() {
        assertEquals(
                0,
                underTest.match("--" + LONG_OPTION),
                "match should return 0 when long option matches");
    }

    @Test
    void matchWhenShortOptionDoesNotMatch() {
        assertEquals(
                -1,
                underTest.match("-a"),
                "match should return -1 when long option does not match");
    }

    @Test
    void matchWhenShortOptionDoesNotContainMatch() {
        assertEquals(
                -1,
                underTest.match("-abcde"),
                "match should return -1 when long option does not match");
    }

    @Test
    void matchWhenShortOptionMatches() {
        assertEquals(
                0,
                underTest.match("-" + SHORT_OPTION),
                "match should return 0 when long option matches");
    }

    @Test
    void matchWhenShortOptionContainsMatch() {
        assertEquals(
                0,
                underTest.match("-abcde" + SHORT_OPTION),
                "match should return 0 when long option contains match");
    }

    @Test
    void matchWhenPositionalOptionDoesNotMatch() {
        assertEquals(
                -1,
                underTest.match("not-an-option"),
                "match should return -1 when positional option does not match");
    }

    @Test
    void matchWhenPositionalOptionMatches() {
        assertEquals(
                0,
                underTest.match(LONG_OPTION),
                "match should return 0 when positional option matches");
    }

    @Test
    void parseWhenCorrectNumberOfParameters() {
        assertTrue(
                underTest.parse(),
                "parse should return true when no arguments are passed");
    }

    @Test
    void parseWhenIncorrectNumberOfParameters() {
        final String parameter = "parameter";
        final String errorMessage = String.format("Incorrect number of arguments passed to option"
                    + " \"%s\". Expected %d but was %d, [%s]",
                LONG_OPTION,
                0,
                1,
                parameter);

        final Throwable exception = assertThrows(
                IllegalArgumentException.class,
                () -> underTest.parse(parameter),
                "parse should throw IllegalArgumentException when arguments are passed");

        assertEquals(
                errorMessage,
                exception.getMessage(),
                "exception message should equal expected");
    }

    @Test
    void toStringTest() {
        assertEquals(
                "-s, --long-option       This is an option used for testing.",
                underTest.toString(),
                "toString should equal expected");
    }
}
