package blue.sparse.minecraft.core.i18n

import blue.sparse.minecraft.core.extensions.getPluginLocale
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

data class LocalizedString(
		val plugin: Plugin,
		val key: String,
		val placeholders: Map<String, Any>
) {

	val default: String?
		get() = PluginLocale.default(plugin)[key, placeholders]

	operator fun get(player: Player): String? {
		return player.getPluginLocale(plugin)[key, placeholders]
	}
}