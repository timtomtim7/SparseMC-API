package blue.sparse.minecraft.commands

import blue.sparse.minecraft.core.extensions.getPluginLocale
import blue.sparse.minecraft.core.extensions.sendMessage
import blue.sparse.minecraft.core.i18n.PluginLocale
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

sealed class CommandContext(val command: Command, val sender: CommandSender) {

	val plugin: Plugin
		get() = command.plugin

	val locale: PluginLocale
		get() = (sender as? Player)?.getPluginLocale(plugin) ?: PluginLocale.default(plugin)

}

class Execute(command: Command, sender: CommandSender) : CommandContext(command, sender) {

	fun error(): Nothing = throw ContextEscape()

	fun errorRaw(vararg parts: Any): Nothing {
		replyRaw(*parts)
		error()
	}

	fun error(key: String, placeholders: Map<String, Any>, default: String = "MISSING_LOCALE_KEY[$key]"): Nothing {
		reply(key, placeholders, default)
		error()
	}

	fun error(key: String, vararg placeholders: Pair<String, Any>): Nothing {
		reply(key, *placeholders)
		error()
	}


	fun replyRaw(vararg parts: Any) {
		sender.sendMessage(*parts)
	}

	fun reply(key: String, placeholders: Map<String, Any>, default: String = "MISSING_LOCALE_KEY[$key]") {
		sender.sendMessage(locale[key, placeholders] ?: default)
	}

	fun reply(key: String, vararg placeholders: Pair<String, Any>) {
		reply(key, placeholders.toMap())
	}
}

class TabComplete(command: Command, sender: CommandSender) : CommandContext(command, sender)

internal class ContextEscape: Throwable()