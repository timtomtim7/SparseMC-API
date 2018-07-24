package blue.sparse.minecraft.persistent

import blue.sparse.minecraft.core.data.nbt.Compound
import blue.sparse.minecraft.core.extensions.server
import org.bukkit.plugin.Plugin

open class Persistent(compound: Compound) {

	private val data = HashMap<String, PersistentPlugin>()

	var lastAccessed = System.currentTimeMillis()
		private set

	internal val compound
		get() = Compound {
			for ((key, plugin) in data)
				compound(key, plugin.compound)
		}

	init {
		for (key in compound.keys())
			data[key] = PersistentPlugin(key, compound.optionalCompound(key) ?: continue)
	}

	operator fun get(plugin: Plugin): PersistentPlugin {
		return data.getOrPut(plugin.name) { PersistentPlugin(plugin.name, Compound()) }
	}

	inner class PersistentPlugin(val pluginName: String, compound: Compound) {

		private val data = HashMap<String, Compound>()

		val plugin: Plugin
			get() = server.pluginManager.getPlugin(pluginName)

		internal val compound
			get() = Compound {
				for ((key, compound) in data)
					compound(key, compound)
			}

		init {
			for (key in compound.keys())
				data[key] = compound.optionalCompound(key) ?: continue
		}

		operator fun get(name: String): Compound {
			lastAccessed = System.currentTimeMillis()
			return data.getOrPut(name) { Compound() }
		}

	}

}