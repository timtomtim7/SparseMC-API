package blue.sparse.minecraft.persistent.persistents

import blue.sparse.minecraft.core.data.nbt.Compound
import blue.sparse.minecraft.persistent.Persistent
import org.bukkit.Chunk
import java.io.File

class ChunkPersistent internal constructor(
		val chunk: Chunk,
		compound: Compound
) : Persistent(compound) {

	fun save() {
		val worldFile = File(WorldPersistent.folder, chunk.world.uid.toString()).apply { mkdirs(); mkdir() }
		val chunksFolder = File(worldFile, "chunks").apply { mkdirs(); mkdir() }
		compound.write(File(chunksFolder, "${chunk.x} ${chunk.z}.dat"))
	}

	companion object {
		internal fun load(chunk: Chunk): ChunkPersistent {
			val worldFile = File(WorldPersistent.folder, chunk.world.uid.toString()).apply { mkdirs(); mkdir() }
			val chunksFolder = File(worldFile, "chunks").apply { mkdirs(); mkdir() }
			val file = File(chunksFolder, "${chunk.x} ${chunk.z}.dat")
			val compound = if (file.exists())
				Compound.read(file)
			else Compound()

			return ChunkPersistent(chunk, compound)
		}
	}
}