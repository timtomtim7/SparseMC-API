package blue.sparse.minecraft.persistent.extensions

import blue.sparse.minecraft.core.PluginProvided
import blue.sparse.minecraft.core.data.nbt.Compound
import blue.sparse.minecraft.persistent.PersistentModule
import org.bukkit.block.Block
import org.bukkit.plugin.Plugin

fun Block.persistent(plugin: Plugin): Compound {
	return PersistentModule[plugin].blocks[this].compound
}

inline fun Block.persistent(
		plugin: Plugin,
		save: Boolean = true,
		body: Compound.() -> Unit
) {
	PersistentModule[plugin].blocks[this].apply {
		compound.body()
		if(save)
			save()
	}
}

fun Block.persistentProvided(plugin: PluginProvided<*>): Compound {
	return PersistentModule[plugin.plugin].blocks[this].compound
}

inline fun Block.persistentProvided(
		plugin: PluginProvided<*>,
		save: Boolean = true,
		body: Compound.() -> Unit
) {
	persistent(plugin.plugin, save, body)
}