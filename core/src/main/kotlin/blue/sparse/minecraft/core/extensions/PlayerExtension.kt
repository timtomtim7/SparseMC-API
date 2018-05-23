package blue.sparse.minecraft.core.extensions

import blue.sparse.minecraft.core.i18n.PluginLocale
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

fun Player.getPluginLocale(plugin: Plugin): PluginLocale {
	return PluginLocale[plugin, player.locale]
}

fun Player.sendMessage(plugin: Plugin, key: String, placeholders: Map<String, Any>) {
	sendMessage(getPluginLocale(plugin)[key, placeholders])
}

fun Player.sendMessage(plugin: Plugin, key: String, vararg placeholders: Pair<String, Any>) {
	sendMessage(plugin, key, placeholders.toMap())
}

fun Player.sendColoredMessage(raw: String) {
	sendMessage(raw.colored)
}