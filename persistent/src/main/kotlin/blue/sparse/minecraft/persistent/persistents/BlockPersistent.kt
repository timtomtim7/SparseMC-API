package blue.sparse.minecraft.persistent.persistents

import blue.sparse.minecraft.SparseMCAPIPlugin
import blue.sparse.minecraft.core.data.nbt.Compound
import blue.sparse.minecraft.core.extensions.server
import blue.sparse.minecraft.persistent.Persistent
import blue.sparse.minecraft.persistent.PersistentModule
import blue.sparse.minecraft.persistent.extensions.persistent
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPistonExtendEvent
import java.io.File
import java.nio.file.Files

class BlockPersistent internal constructor(
		val block: Block,
		compound: Compound
) : Persistent(compound) {

	fun save() {
		block.chunk.persistent(SparseMCAPIPlugin.getPlugin()) {
			editCompound("Block Persistent") {
				compound("${block.x} ${block.y} ${block.z}", compound)
			}
		}
	}

	companion object {
		internal fun load(block: Block): BlockPersistent {
			val blockPersistentCompound = block.chunk.persistent[SparseMCAPIPlugin.getPlugin()]
					.compound("Block Persistent")
			return BlockPersistent(block, blockPersistentCompound
					.optionalCompound("${block.x} ${block.y} ${block.z}") ?: Compound())
		}
	}

}