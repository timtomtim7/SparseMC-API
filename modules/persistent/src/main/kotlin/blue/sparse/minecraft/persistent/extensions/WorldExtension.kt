package blue.sparse.minecraft.persistent.extensions

import blue.sparse.minecraft.core.PluginProvided
import blue.sparse.minecraft.core.data.nbt.Compound
import blue.sparse.minecraft.persistent.PersistentModule
import org.bukkit.World
import org.bukkit.plugin.Plugin

fun World.persistent(plugin: Plugin): Compound {
	return PersistentModule[plugin].worlds[this].compound
}

inline fun World.persistent(
		plugin: Plugin,
		save: Boolean = true,
		body: Compound.() -> Unit
) {
	PersistentModule[plugin].worlds[this].apply {
		compound.body()
		if(save)
			save()
	}
}

fun World.persistentProvided(plugin: PluginProvided<*>): Compound {
	return PersistentModule[plugin.plugin].worlds[this].compound
}

inline fun World.persistentProvided(
		plugin: PluginProvided<*>,
		save: Boolean = true,
		body: Compound.() -> Unit
) {
	persistent(plugin.plugin, save, body)
}