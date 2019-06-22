package blue.sparse.minecraft.persistent.extensions

import blue.sparse.minecraft.core.PluginProvided
import blue.sparse.minecraft.core.data.nbt.Compound
import blue.sparse.minecraft.persistent.PersistentModule
import org.bukkit.Chunk
import org.bukkit.plugin.Plugin

fun Chunk.persistent(plugin: Plugin): Compound {
	return PersistentModule[plugin].chunks[this].compound
}

inline fun Chunk.persistent(
		plugin: Plugin,
		save: Boolean = true,
		body: Compound.() -> Unit
) {
	PersistentModule[plugin].chunks[this].apply {
		compound.body()
		if(save)
			save()
	}
}

fun Chunk.persistentProvided(plugin: PluginProvided<*>): Compound {
	return PersistentModule[plugin.plugin].chunks[this].compound
}

inline fun Chunk.persistentProvided(
		plugin: PluginProvided<*>,
		save: Boolean = true,
		body: Compound.() -> Unit
) {
	persistent(plugin.plugin, save, body)
}