package blue.sparse.minecraft.persistent.persistents

import blue.sparse.minecraft.core.data.nbt.Compound
import blue.sparse.minecraft.persistent.Persistent
import blue.sparse.minecraft.persistent.PersistentModule
import org.bukkit.plugin.Plugin
import java.io.File

class PluginPersistent internal constructor(
		val plugin: Plugin,
		compound: Compound
) : Persistent(compound) {

	fun save() = compound.write(File(folder, "${plugin.name}Plugin.dat"))

	companion object {

		val folder get() = File(PersistentModule.folder, "plugins").apply { mkdirs() }

		internal fun load(plugin: Plugin): PluginPersistent {
			val file = File(folder, "${plugin.name}Plugin.dat")
			val compound = if (file.exists())
				Compound.read(file)
			else
				Compound()
			return PluginPersistent(plugin, compound)
		}
	}

}