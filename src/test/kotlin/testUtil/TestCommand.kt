package testUtil

import com.ixibot.command.Command

class TestCommand : Command<TestCommandEvent, TestCommandEvent.TestCommandEventBuilder>(
    aboutText = "A dummy command for unit tests",
    name = "test",
    usageText = "test [options]",
    options = emptyList()
) {
    override fun getAccumulator(): TestCommandEvent.TestCommandEventBuilder {
        return TestCommandEvent.TestCommandEventBuilder()
    }
}
