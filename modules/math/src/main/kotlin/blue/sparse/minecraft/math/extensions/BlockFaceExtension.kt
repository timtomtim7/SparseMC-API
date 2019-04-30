package blue.sparse.minecraft.math.extensions

import blue.sparse.math.vector.floats.vec3f
import org.bukkit.block.BlockFace
import org.bukkit.util.Vector

fun BlockFace.toVector3f() = vec3f(modX.toFloat(), modY.toFloat(), modZ.toFloat())
fun BlockFace.toVector() = Vector(modX, modY, modZ)

val BlockFace.relativeLeft get() = when(this) {
	BlockFace.NORTH -> BlockFace.WEST
	BlockFace.WEST -> BlockFace.SOUTH
	BlockFace.SOUTH -> BlockFace.EAST
	BlockFace.EAST -> BlockFace.NORTH
	BlockFace.SOUTH_EAST -> BlockFace.NORTH_EAST
	BlockFace.NORTH_EAST -> BlockFace.NORTH_WEST
	BlockFace.NORTH_WEST -> BlockFace.SOUTH_WEST
	BlockFace.SOUTH_WEST -> BlockFace.SOUTH_EAST
	BlockFace.UP -> BlockFace.WEST
	BlockFace.DOWN -> BlockFace.WEST
	else -> throw IllegalArgumentException(this.name)
}

val BlockFace.relativeRight get() = when(this) {
	BlockFace.WEST -> BlockFace.NORTH
	BlockFace.NORTH -> BlockFace.EAST
	BlockFace.EAST -> BlockFace.SOUTH
	BlockFace.SOUTH -> BlockFace.WEST
	BlockFace.NORTH_EAST -> BlockFace.SOUTH_EAST
	BlockFace.SOUTH_EAST -> BlockFace.SOUTH_WEST
	BlockFace.SOUTH_WEST -> BlockFace.NORTH_WEST
	BlockFace.NORTH_WEST -> BlockFace.NORTH_EAST
	BlockFace.UP -> BlockFace.EAST
	BlockFace.DOWN -> BlockFace.EAST
	else -> throw IllegalArgumentException(this.name)
}

val BlockFace.relativeUp get() = when(this) {
	BlockFace.WEST -> BlockFace.UP
	BlockFace.NORTH -> BlockFace.UP
	BlockFace.EAST -> BlockFace.UP
	BlockFace.SOUTH -> BlockFace.UP
	BlockFace.NORTH_EAST -> BlockFace.UP
	BlockFace.SOUTH_EAST -> BlockFace.UP
	BlockFace.SOUTH_WEST -> BlockFace.UP
	BlockFace.NORTH_WEST -> BlockFace.UP
	BlockFace.UP -> BlockFace.NORTH
	BlockFace.DOWN -> BlockFace.SOUTH
	else -> throw IllegalArgumentException(this.name)
}

val BlockFace.relativeDown get() = when(this) {
	BlockFace.WEST -> BlockFace.DOWN
	BlockFace.NORTH -> BlockFace.DOWN
	BlockFace.EAST -> BlockFace.DOWN
	BlockFace.SOUTH -> BlockFace.DOWN
	BlockFace.NORTH_EAST -> BlockFace.DOWN
	BlockFace.SOUTH_EAST -> BlockFace.DOWN
	BlockFace.SOUTH_WEST -> BlockFace.DOWN
	BlockFace.NORTH_WEST -> BlockFace.DOWN
	BlockFace.UP -> BlockFace.SOUTH
	BlockFace.DOWN -> BlockFace.NORTH
	else -> throw IllegalArgumentException(this.name)
}