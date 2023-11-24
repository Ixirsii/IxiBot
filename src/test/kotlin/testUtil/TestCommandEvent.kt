package testUtil

import com.ixibot.event.CommandEvent

class TestCommandEvent(
    isHelp: Boolean,
    isValid: Boolean,
    val testValue: Boolean
) : CommandEvent<TestCommandEvent, TestCommandEvent.TestCommandEventBuilder>(isHelp, isValid) {

    override fun toBuilder(): Builder<TestCommandEvent, TestCommandEventBuilder> {
        return TestCommandEventBuilder()
    }

    data class TestCommandEventBuilder(var testValue: Boolean = false): Builder<TestCommandEvent, TestCommandEventBuilder>() {
        override fun build(): TestCommandEvent {
            return TestCommandEvent(isHelp, isValid, testValue)
        }

        override fun self(): TestCommandEventBuilder {
            return this
        }

        fun testValue(testValue: Boolean): TestCommandEventBuilder = apply { this.testValue = testValue }
    }
}
