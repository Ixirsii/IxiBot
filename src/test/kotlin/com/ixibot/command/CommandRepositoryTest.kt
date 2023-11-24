package com.ixibot.command

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import testUtil.TestCommand
import testUtil.TestCommandEvent

private const val COMMAND_PREFIX: String = "."
// TODO: Add tests for multiple short options
private const val ALL_SHORT_OPTIONS: String = "add_roll -hVAR"

class CommandRepositoryTest {
    private lateinit var underTest: CommandRepository

    @BeforeEach
    fun setup() {
        underTest = CommandRepository(commandPrefix = COMMAND_PREFIX)
    }

    @Test
    fun `GIVEN valid command WHEN isCommand THEN returns true`() {
        Assertions.assertTrue(underTest.isCommand(".test"),
            "String starting with command prefix should be a command")
    }

    @Test
    fun `GIVEN invalid command WHEN isCommand THEN returns false`() {
        Assertions.assertFalse(underTest.isCommand("test"),
            "String not starting with command prefix should not be a command")
    }

    @Test
    fun `GIVEN registered command WHEN parse THEN returns event`() {
        // Given
        underTest.register(TestCommand())

        // When
        val actual = underTest.parse(".test")

        // Then
        // TODO: Add more assertions for returned event
        Assertions.assertTrue(actual is TestCommandEvent, "Result should be of expected type")
    }

    @Test
    fun `GIVEN unregistered command WHEN parse THEN throws IAE`() {
        Assertions.assertThrows(
            IllegalArgumentException::class.java,
            { underTest.parse(".add_role") },
            "Should throw IllegalArgumentException when parsing unregistered command")
    }

    @Test
    fun `GIVEN registered command WHEN unregister Command THEN returns true`() {
        // Given
        val addRole: Command<TestCommandEvent, TestCommandEvent.TestCommandEventBuilder> = TestCommand()
        underTest.register(addRole)

        // When
        val actual: Boolean = underTest.unregister(addRole)

        // Then
        Assertions.assertTrue(actual, "Should unregister command")
    }

    @Test
    fun `GIVEN registered command WHEN unregister String THEN returns true`() {
        // Given
        val addRole: Command<TestCommandEvent, TestCommandEvent.TestCommandEventBuilder> = TestCommand()
        underTest.register(addRole)

        // When
        val actual: Boolean = underTest.unregister(addRole.name)

        // Then
        Assertions.assertTrue(actual, "Should unregister command")
    }

    @Test
    fun `GIVEN unregistered command WHEN unregister Command THEN returns false`() {
        // Given
        val addRole: Command<TestCommandEvent, TestCommandEvent.TestCommandEventBuilder> = TestCommand()

        // When
        val actual: Boolean = underTest.unregister(addRole)

        // Then
        Assertions.assertFalse(actual, "Should not unregister command")
    }

    @Test
    fun `GIVEN unregistered command WHEN unregister String THEN returns false`() {
        // Given
        val addRole: Command<TestCommandEvent, TestCommandEvent.TestCommandEventBuilder> = TestCommand()

        // When
        val actual: Boolean = underTest.unregister(addRole.name)

        // Then
        Assertions.assertFalse(actual, "Should not unregister command")
    }
}
