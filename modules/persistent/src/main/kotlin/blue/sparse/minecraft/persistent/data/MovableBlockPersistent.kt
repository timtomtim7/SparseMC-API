package blue.sparse.minecraft.persistent.data

import blue.sparse.minecraft.core.data.nbt.Compound
import blue.sparse.minecraft.persistent.PersistentPlugin
import org.bukkit.Chunk
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPistonExtendEvent
import org.bukkit.event.block.BlockPistonRetractEvent
import java.util.WeakHashMap

class MovableBlockPersistent(
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

		private val blocks = WeakHashMap<Block, MovableBlockPersistent>()

		override fun get(value: Block): Persistent<Block> {
			return blocks.getOrPut(value) {
				val chunk = getChunkPersistent(plugin, value).compound
				MovableBlockPersistent(this, value, getCompound(value, chunk))
			}
		}

		override fun saveAll() = blocks.values.forEach(MovableBlockPersistent::save)

	}

	companion object: Listener {
//		init {
//			PersistentModule.registerListener(this)
//		}

		internal fun getChunkPersistent(plugin: PersistentPlugin, block: Block): Persistent<Chunk> {
			return plugin.chunks[block.chunk]
		}

		internal fun getCompound(block: Block, root: Compound): Compound {
			val blocks = root.defaultCompound("__mblocks")
			val x = blocks.defaultCompound(block.x.toString())
			val y = x.defaultCompound(block.y.toString())
			return y.defaultCompound(block.z.toString())
		}

		internal fun setCompound(block: Block, root: Compound, value: Compound) {
			val blocks = root.defaultCompound("__mblocks")
			val x = blocks.defaultCompound(block.x.toString())
			val y = x.defaultCompound(block.y.toString())
			y.compound(block.z.toString(), value)
//			return y.defaultCompound(block.z.toString())
		}

		@EventHandler
		private fun onPistonExtend(e: BlockPistonExtendEvent) {

		}

		@EventHandler
		private fun onPistonRetract(e: BlockPistonRetractEvent) {

		}
	}

}