package blue.sparse.minecraft.core.util

import blue.sparse.minecraft.core.extensions.server
import org.bukkit.World
import org.bukkit.block.Block
import java.util.UUID

data class LazyBlock(val worldID: UUID, val x: Int, val y: Int, val z: Int) {

	val world: World
		get() = server.getWorld(worldID)

	val block: Block
		get() = world.getBlockAt(x, y, z)

}