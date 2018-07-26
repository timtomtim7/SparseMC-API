package blue.sparse.minecraft.persistent

import blue.sparse.minecraft.core.extensions.server
import blue.sparse.minecraft.module.*
import org.bukkit.plugin.Plugin
import java.util.WeakHashMap

@ModuleDefinition
object PersistentModule : AbstractModule(ModuleType.PERSISTENT) {

	private val persistent = WeakHashMap<Plugin, PersistentPlugin>()

	operator fun get(plugin: Plugin): PersistentPlugin {
		return persistent.getOrPut(plugin) { PersistentPlugin(plugin) }
	}

	fun saveAll() {
		persistent.values.forEach {
			it.players.saveAll()
			it.plugins.saveAll()
		}
	}

	override fun onEnable() {
		server.scheduler.scheduleSyncRepeatingTask(plugin, {
			println("Persistent data has been auto-saved.")
			saveAll()
		}, 0, 5 * 60 * 20L)
	}

	override fun onDisable() {}
}