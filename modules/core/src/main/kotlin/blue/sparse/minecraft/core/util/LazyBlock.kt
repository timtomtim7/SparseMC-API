package blue.sparse.minecraft.core.util

import blue.sparse.minecraft.core.extensions.server
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.material.MaterialData
import java.util.UUID

data class LazyBlock(val worldID: UUID, val x: Int, val y: Int, val z: Int) {

	constructor(block: Block) : this(block.world.uid, block.x, block.y, block.z)

	val world: World
		get() = server.getWorld(worldID)

	val block: Block
		get() = world.getBlockAt(x, y, z)

	val materialData: MaterialData
		get() = block.state.data

}