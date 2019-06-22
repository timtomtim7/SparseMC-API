package blue.sparse.minecraft.persistent.extensions

import blue.sparse.minecraft.core.PluginProvided
import blue.sparse.minecraft.core.data.nbt.Compound
import blue.sparse.minecraft.persistent.PersistentModule
import org.bukkit.OfflinePlayer
import org.bukkit.plugin.Plugin

fun OfflinePlayer.persistent(plugin: Plugin): Compound {
	return PersistentModule[plugin].players[this].compound
}

inline fun OfflinePlayer.persistent(
		plugin: Plugin,
		save: Boolean = true,
		body: Compound.() -> Unit
) {
	PersistentModule[plugin].players[this].apply {
		compound.body()
		if(save)
			save()
	}
}

fun OfflinePlayer.persistentProvided(plugin: PluginProvided<*>): Compound {
	return PersistentModule[plugin.plugin].players[this].compound
}

inline fun OfflinePlayer.persistentProvided(
		plugin: PluginProvided<*>,
		save: Boolean = true,
		body: Compound.() -> Unit
) {
	persistent(plugin.plugin, save, body)
}