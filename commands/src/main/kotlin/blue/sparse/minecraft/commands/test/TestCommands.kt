package blue.sparse.minecraft.commands.test

import blue.sparse.minecraft.commands.*

object TestCommands {

	@Command.Description("A test command.")
	fun Execute.test() {
		replyRaw("Hello?")
	}

	fun TabComplete.test(): List<String> {
		return listOf("hello")
	}

}