import org.bukkit.command.CommandSender

object TestCommands {

	@Command("test", "hello")
	fun Execute.test() {
		sender.sendMessage("This is a test!")
	}

	fun Execute.hello(name: String) {
		sender.sendMessage("Hello, $name!")
	}

}

annotation class Command(vararg val names: String)

sealed class CommandContext(val sender: CommandSender) {
	fun reply(vararg message: Any) {}
}

class Execute(sender: CommandSender) : CommandContext(sender)
class TabComplete(sender: CommandSender) : CommandContext(sender)

fun register(func: (Execute) -> Unit) {}