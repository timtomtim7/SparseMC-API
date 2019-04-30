package blue.sparse.minecraft.persistent.extensions

import blue.sparse.minecraft.core.PluginProvided
import blue.sparse.minecraft.core.data.nbt.Compound
import blue.sparse.minecraft.persistent.PersistentModule
import org.bukkit.plugin.Plugin

val PluginProvided<*>.persistent
	get() = persistent(plugin)

fun PluginProvided<*>.persistent(plugin: Plugin): Compound {
	return PersistentModule[plugin].plugins[plugin].compound
}

inline fun PluginProvided<*>.persistent(
		plugin: Plugin = this.plugin,
		save: Boolean = true,
		body: Compound.() -> Unit
) {
	PersistentModule[plugin].plugins[plugin].apply {
		compound.body()
		if (save)
			save()
	}
}