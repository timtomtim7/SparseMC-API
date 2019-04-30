package blue.sparse.minecraft.math.extensions

import blue.sparse.math.vector.floats.Vector3f
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.util.NumberConversions
import org.bukkit.util.Vector

fun Vector3f.toLocation(world: World): Location {
	return Location(world, x.toDouble(), y.toDouble(), z.toDouble())
}

fun Vector3f.toBlock(world: World): Block {
	return world.getBlockAt(
			NumberConversions.floor(x.toDouble()),
			NumberConversions.floor(y.toDouble()),
			NumberConversions.floor(z.toDouble())
	)
}

fun Vector3f.toBukkitVector(): Vector {
	return Vector(x, y, z)
}