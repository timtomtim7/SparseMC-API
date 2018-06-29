package blue.sparse.minecraft.math.extensions

import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import kotlin.math.max
import kotlin.math.min

val Block.center: Location
	get() = location.add(0.5, 0.5, 0.5)

fun min(a: Block, b: Block): Block {
	if (a.world != b.world)
		throw IllegalArgumentException("Differing worlds.")

	return a.world.getBlockAt(
			min(a.x, b.x),
			min(a.y, b.y),
			min(a.z, b.z)
	)
}

fun max(a: Block, b: Block): Block {
	if (a.world != b.world)
		throw IllegalArgumentException("Differing worlds.")

	return a.world.getBlockAt(
			max(a.x, b.x),
			max(a.y, b.y),
			max(a.z, b.z)
	)
}

fun Block.adjacent(): Set<Block> {
	return BlockFace.values().take(6).mapTo(HashSet(), this::getRelative)
}

operator fun Block.plus(other: Block) = world.getBlockAt(x + other.x, y + other.y, z + other.z)
operator fun Block.minus(other: Block) = world.getBlockAt(x - other.x, y - other.y, z - other.z)
operator fun Block.times(other: Block) = world.getBlockAt(x * other.x, y * other.y, z * other.z)
operator fun Block.div(other: Block) = world.getBlockAt(x / other.x, y / other.y, z / other.z)
operator fun Block.rem(other: Block) = world.getBlockAt(x % other.x, y % other.y, z % other.z)

operator fun Block.plus(other: Int) = world.getBlockAt(x + other, y + other, z + other)
operator fun Block.minus(other: Int) = world.getBlockAt(x - other, y - other, z - other)
operator fun Block.times(other: Int) = world.getBlockAt(x * other, y * other, z * other)
operator fun Block.div(other: Int) = world.getBlockAt(x / other, y / other, z / other)
operator fun Block.rem(other: Int) = world.getBlockAt(x % other, y % other, z % other)