package blue.sparse.minecraft.persistent

import blue.sparse.minecraft.core.extensions.server
import blue.sparse.minecraft.module.AbstractModule
import blue.sparse.minecraft.module.ModuleType
import org.bukkit.plugin.Plugin
import java.util.WeakHashMap

object PersistentModule : AbstractModule(ModuleType.PERSISTENT) {

	private val persistent = WeakHashMap<Plugin, PersistentPlugin>()

	operator fun get(plugin: Plugin): PersistentPlugin {
		return persistent.getOrPut(plugin) { PersistentPlugin(plugin) }
	}

	fun saveAll() {
		persistent.values.forEach {
			it.players.saveAll()
			it.plugins.saveAll()
			it.blocks.saveAll()
			it.chunks.saveAll()
			it.worlds.saveAll()
		}
	}

	override fun onEnable() {
		server.scheduler.scheduleSyncRepeatingTask(plugin, {
			println("Persistent data has been auto-saved.")
			saveAll()
		}, 0, 5 * 60 * 20L)
	}

	override fun onDisable() {
		saveAll()
	}
}