package blue.sparse.minecraft.persistent.data

import blue.sparse.minecraft.core.data.nbt.Compound
import blue.sparse.minecraft.persistent.PersistentPlugin
import org.bukkit.World
import org.bukkit.plugin.Plugin
import java.io.File
import java.util.WeakHashMap

class WorldPersistent(
		manager: Manager,
		world: World,
		override val compound: Compound
): Persistent<World>(manager, world) {

	private val file = File(getFolder(world, manager.plugin.plugin), "world.dat")

	override fun save() {
		compound.write(file)
	}

	class Manager(plugin: PersistentPlugin): PersistentManager<World>(plugin) {

		private val worlds = WeakHashMap<World, WorldPersistent>()

//		override val folder: File
//			get() = File(super.folder, "worlds").apply { mkdirs() }

		override fun get(value: World): Persistent<World> {
			return worlds.getOrPut(value) {
				val file = File(folder, "${value.uid}.dat")
				WorldPersistent(this, value, Compound.readOrCreate(file))
			}
		}

		override fun saveAll() = worlds.values.forEach(WorldPersistent::save)

	}

	companion object {
		fun getFolder(world: World): File {
			return File(world.worldFolder, "persistent").apply { mkdirs() }
		}

		fun getFolder(world: World, plugin: Plugin): File {
			return File(getFolder(world), plugin.name).apply { mkdirs() }
		}
	}

}