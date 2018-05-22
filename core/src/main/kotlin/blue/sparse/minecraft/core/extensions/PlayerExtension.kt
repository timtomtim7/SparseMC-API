package blue.sparse.minecraft.core.extensions

import blue.sparse.minecraft.core.i18n.PluginLocale
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

fun Player.getPluginLocale(plugin: Plugin): PluginLocale {
	return PluginLocale[plugin, player.locale]
}

fun Player.send(plugin: Plugin, key: String, placeholders: Map<String, Any>) {
	sendMessage(getPluginLocale(plugin)[key, placeholders])
}

fun Player.send(plugin: Plugin, key: String, vararg placeholders: Pair<String, Any>) {
	send(plugin, key, placeholders.toMap())
}

fun Player.send(raw: String) {
	sendMessage(raw)
}

fun Player.sendColored(raw: String) {
	sendMessage(raw.colored)
}