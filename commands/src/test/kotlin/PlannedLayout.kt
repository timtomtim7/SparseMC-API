import blue.sparse.minecraft.core.extensions.getPluginLocale
import blue.sparse.minecraft.core.i18n.PluginLocale
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

object TestCommands {

	fun Execute.test() {
		sender.sendMessage("This is a test!")
	}

	@Command.Name("test")
	object TestCommandGroup: CommandGroup {

		// The annotation marks this command as the default, so executing `/test` would run this.
		@Command.Default
		fun Execute.default() {
			reply("testMessage")
		}

		// No annotation required, this can be executed with `/test hello <string>`
		fun Execute.hello(name: String) {
			reply("greeting", "name" to name)
		}

	}
}

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