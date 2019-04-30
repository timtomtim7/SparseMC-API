package blue.sparse.minecraft.persistent

import blue.sparse.minecraft.persistent.data.*
import org.bukkit.plugin.Plugin
import java.io.File

class PersistentPlugin internal constructor(val plugin: Plugin) {

	val folder: File
		get() = File(plugin.dataFolder, "persistent").apply { mkdirs() }

	val players = PlayerPersistent.Manager(this)
	val plugins = PluginPersistent.Manager(this)
	val worlds  = WorldPersistent.Manager(this)
	val chunks  = ChunkPersistent.Manager(this)
	val blocks  = BlockPersistent.Manager(this)

}