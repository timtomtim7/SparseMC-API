package blue.sparse.minecraft.math.extensions

import blue.sparse.math.vector.floats.Vector3f
import blue.sparse.math.vector.floats.vec3f
import org.bukkit.Location
import org.bukkit.block.BlockFace
import org.bukkit.util.Vector

fun Vector.toVector3f() = vec3f(x.toFloat(), y.toFloat(), z.toFloat())

fun Vector.copy(x: Double, y: Double, z: Double) = Vector(x, y, z)
fun Vector.copy(x: Float, y: Float, z: Float) = copy(x.toDouble(), y.toDouble(), z.toDouble())

fun Vector.toBlockFace(): BlockFace {
	return BlockFace.values().first {
		it.modX == blockX && it.modY == blockY && it.modZ == blockZ
	}
}

operator fun Vector.plus(other: Vector3f) = copy(x + other.x, y + other.y, z + other.z)
operator fun Vector.minus(other: Vector3f) = copy(x - other.x, y - other.y, z - other.z)
operator fun Vector.times(other: Vector3f) = copy(x * other.x, y * other.y, z * other.z)
operator fun Vector.div(other: Vector3f) = copy(x / other.x, y / other.y, z / other.z)
operator fun Vector.rem(other: Vector3f) = copy(x % other.x, y % other.y, z % other.z)

operator fun Vector.plus(other: Location) = copy(x + other.x, y + other.y, z + other.z)
operator fun Vector.minus(other: Location) = copy(x - other.x, y - other.y, z - other.z)
operator fun Vector.times(other: Location) = copy(x * other.x, y * other.y, z * other.z)
operator fun Vector.div(other: Location) = copy(x / other.x, y / other.y, z / other.z)
operator fun Vector.rem(other: Location) = copy(x % other.x, y % other.y, z % other.z)

operator fun Vector.plus(other: Vector) = copy(x + other.x, y + other.y, z + other.z)
operator fun Vector.minus(other: Vector) = copy(x - other.x, y - other.y, z - other.z)
operator fun Vector.times(other: Vector) = copy(x * other.x, y * other.y, z * other.z)
operator fun Vector.div(other: Vector) = copy(x / other.x, y / other.y, z / other.z)
operator fun Vector.rem(other: Vector) = copy(x % other.x, y % other.y, z % other.z)

operator fun Vector.plus(other: Double) = copy(x + other, y + other, z + other)
operator fun Vector.minus(other: Double) = copy(x - other, y - other, z - other)
operator fun Vector.times(other: Double) = copy(x * other, y * other, z * other)
operator fun Vector.div(other: Double) = copy(x / other, y / other, z / other)
operator fun Vector.rem(other: Double) = copy(x % other, y % other, z % other)