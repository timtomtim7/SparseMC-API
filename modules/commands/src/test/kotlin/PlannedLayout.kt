import blue.sparse.minecraft.commands.parsing.util.QuotedString
import blue.sparse.minecraft.commands.parsing.util.SpacedString
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

object TestCommands {

	fun Execute.test() {
		sender.sendMessage("This is a test!")
	}

	fun Execute.player(player: OfflinePlayer) {}

	fun Execute.player(value: Int) {}

	fun TabComplete.player(): List<String> {
		return emptyList()
	}


	@Command.Aliases("f")
	object Factions: CommandGroup {
		fun Execute.who(player: OfflinePlayer) {

		}

		fun Execute.map() {

		}

		fun Execute.help() {

		}

		@Command.Default
		fun Execute.default() {

		}
	}

	@Command.Name("test")
	object Test: CommandGroup {

		// The annotation marks this command as the default, so executing `/test` would run this.
		@Command.Default
		fun Execute.default() {
			reply("testMessage")
		}

		fun Execute.hello(name: QuotedString) {
			reply("greeting", "name" to name)
		}

		fun Execute.msg(targets: Either<Player, List<Player>>, message: SpacedString) {

		}
	}
}

annotation class Quoted

interface CommandGroup

class Command(
		val plugin: Plugin,
		val name: String,
		val aliases: Array<out String>,
		val description: String,
		val usage: String
) {
	@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
	annotation class Name(val name: String)

	@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
	annotation class Aliases(vararg val aliases: String)

	@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
	annotation class Description(val description: String)

	@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
	annotation class Usage(val usage: String)

	/**
	 * Used to indicate the default function for a subcommand group
	 */
	@Target(AnnotationTarget.FUNCTION)
	annotation class Default
}

sealed class CommandContext(val command: Command, val sender: CommandSender) {

	val plugin: Plugin
		get() = command.plugin

	val locale: PluginLocale
		get() = (sender as? Player)?.getPluginLocale(plugin) ?: PluginLocale.default(plugin)

}

class Execute(command: Command, sender: CommandSender) : CommandContext(command, sender) {
	fun replyRaw(vararg message: Any) {}

	fun reply(key: String, placeholders: Map<String, Any>) {}
	fun reply(key: String, vararg placeholders: Pair<String, Any>) {}

}
class TabComplete(command: Command, sender: CommandSender) : CommandContext(command, sender)

fun register(func: (Execute) -> Unit) {}