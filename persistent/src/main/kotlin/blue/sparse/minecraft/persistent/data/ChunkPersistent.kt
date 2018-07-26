package blue.sparse.minecraft.persistent.data

import blue.sparse.minecraft.core.data.nbt.Compound
import blue.sparse.minecraft.persistent.PersistentPlugin
import org.bukkit.Chunk
import org.bukkit.World
import org.bukkit.plugin.Plugin
import java.io.File
import java.util.WeakHashMap

class ChunkPersistent(
		manager: Manager,
		chunk: Chunk,
		override val compound: Compound
) : Persistent<Chunk>(manager, chunk) {

	private val file = getFile(chunk, manager.plugin.plugin)

	override fun save() {
		compound.write(file)
	}

	class Manager(plugin: PersistentPlugin) : PersistentManager<Chunk>(plugin) {

		private val chunks = WeakHashMap<Chunk, ChunkPersistent>()

		override fun get(value: Chunk): Persistent<Chunk> {
			return chunks.getOrPut(value) {
				val file = getFile(value, plugin.plugin)
				ChunkPersistent(this, value, Compound.readOrCreate(file))
			}
		}

		override fun saveAll() = chunks.values.forEach(ChunkPersistent::save)

	}

	companion object {

		fun getFolder(world: World, plugin: Plugin): File {
			val chunks = File(WorldPersistent.getFolder(world), "chunks")
			return File(chunks, plugin.name).apply { mkdirs() }
		}

		fun getFile(chunk: Chunk, plugin: Plugin): File {
			return File(getFolder(chunk.world, plugin), "${chunk.x}.${chunk.z}.dat")
		}
	}

}