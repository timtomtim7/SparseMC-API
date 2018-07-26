package blue.sparse.minecraft.persistent.data

import blue.sparse.minecraft.core.data.nbt.Compound
import blue.sparse.minecraft.persistent.PersistentPlugin
import org.bukkit.plugin.Plugin
import java.io.File
import java.util.WeakHashMap

class PluginPersistent(
		manager: Manager,
		plugin: Plugin,
		override val compound: Compound
) : Persistent<Plugin>(manager, plugin) {

	private val file = File(manager.folder, "${plugin.name}.dat")

	override fun save() {
		compound.write(file)
	}

	class Manager(plugin: PersistentPlugin) : PersistentManager<Plugin>(plugin) {

		private val plugins = WeakHashMap<Plugin, PluginPersistent>()

		override val folder: File
			get() = File(super.folder, "players").apply { mkdirs() }

		override fun get(value: Plugin): Persistent<Plugin> {
			return plugins.getOrPut(value) {
				val file = File(folder, "${value.name}.dat")
				PluginPersistent(this, value, Compound.readOrCreate(file))
			}
		}

		override fun saveAll() = plugins.values.forEach(PluginPersistent::save)
	}

}