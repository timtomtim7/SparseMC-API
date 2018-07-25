package blue.sparse.minecraft.persistent

import blue.sparse.minecraft.core.data.nbt.Compound
import blue.sparse.minecraft.core.extensions.server
import org.bukkit.plugin.Plugin

open class Persistent(compound: Compound) {

	private val dataCache = HashMap<Plugin, Compound>()

	var lastAccessed = System.currentTimeMillis()
		private set

	internal val compound
		get() = Compound {
			for ((key, compound) in dataCache)
				compound("${key.name}Plugin", compound)
		}

	init {
		for (key in compound.keys()) {
			val plugin = server.pluginManager.getPlugin(key) ?: continue
			dataCache[plugin] = compound.optionalCompound(plugin.name) ?: continue
		}
	}

	operator fun get(plugin: Plugin): Compound {
		return dataCache.getOrPut(plugin) { Compound() }
	}

	operator fun invoke(plugin: Plugin, body: Compound.() -> Unit): Compound {
		val newCompound = dataCache.getOrPut(plugin) { Compound() }.apply(body)
		dataCache[plugin] = newCompound
		return newCompound
	}
}