package blue.sparse.minecraft.persistent.data

import blue.sparse.minecraft.core.data.nbt.Compound
import blue.sparse.minecraft.persistent.PersistentPlugin
import org.bukkit.Chunk
import org.bukkit.block.Block
import java.util.WeakHashMap

class BlockPersistent(
		manager: Manager,
		block: Block,
		override val compound: Compound
) : Persistent<Block>(manager, block) {

	override fun save() {
		val block = instance!!
		val chunk = getChunkPersistent(manager.plugin, block)
		setCompound(block, chunk.compound, compound)
		chunk.save()
	}

	class Manager(plugin: PersistentPlugin) : PersistentManager<Block>(plugin) {

		private val blocks = WeakHashMap<Block, BlockPersistent>()

		override fun get(value: Block): Persistent<Block> {
			return blocks.getOrPut(value) {
				val chunk = getChunkPersistent(plugin, value).compound
				BlockPersistent(this, value, getCompound(value, chunk))
			}
		}

		override fun saveAll() = blocks.values.forEach(BlockPersistent::save)

	}

	companion object {
		internal fun getChunkPersistent(plugin: PersistentPlugin, block: Block): Persistent<Chunk> {
			return plugin.chunks[block.chunk]
		}

		internal fun getCompound(block: Block, root: Compound): Compound {
			val blocks = root.defaultCompound("__blocks")
			val x = blocks.defaultCompound(block.x.toString())
			val y = x.defaultCompound(block.y.toString())
			return y.defaultCompound(block.z.toString())
		}

		internal fun setCompound(block: Block, root: Compound, value: Compound) {
			val blocks = root.defaultCompound("__blocks")
			val x = blocks.defaultCompound(block.x.toString())
			val y = x.defaultCompound(block.y.toString())
			y.compound(block.z.toString(), value)
//			return y.defaultCompound(block.z.toString())
		}
	}

}