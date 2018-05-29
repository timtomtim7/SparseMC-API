package blue.sparse.minecraft.math.extensions

import blue.sparse.math.vector.floats.Vector3f
import blue.sparse.math.vector.floats.vec3f
import org.bukkit.Location
import org.bukkit.util.Vector

fun Location.toVector3f() = vec3f(x.toFloat(), y.toFloat(), z.toFloat())

fun Location.copy(x: Double, y: Double, z: Double) = Location(world, x, y, z)
fun Location.copy(x: Float, y: Float, z: Float) = copy(x.toDouble(), y.toDouble(), z.toDouble())

operator fun Location.plus(other: Vector3f) = copy(x + other.x, y + other.y, z + other.z)
operator fun Location.minus(other: Vector3f) = copy(x - other.x, y - other.y, z - other.z)
operator fun Location.times(other: Vector3f) = copy(x * other.x, y * other.y, z * other.z)
operator fun Location.div(other: Vector3f) = copy(x / other.x, y / other.y, z / other.z)
operator fun Location.rem(other: Vector3f) = copy(x % other.x, y % other.y, z % other.z)

operator fun Location.plus(other: Location) = copy(x + other.x, y + other.y, z + other.z)
operator fun Location.minus(other: Location) = copy(x - other.x, y - other.y, z - other.z)
operator fun Location.times(other: Location) = copy(x * other.x, y * other.y, z * other.z)
operator fun Location.div(other: Location) = copy(x / other.x, y / other.y, z / other.z)
operator fun Location.rem(other: Location) = copy(x % other.x, y % other.y, z % other.z)

operator fun Location.plus(other: Vector) = copy(x + other.x, y + other.y, z + other.z)
operator fun Location.minus(other: Vector) = copy(x - other.x, y - other.y, z - other.z)
operator fun Location.times(other: Vector) = copy(x * other.x, y * other.y, z * other.z)
operator fun Location.div(other: Vector) = copy(x / other.x, y / other.y, z / other.z)
operator fun Location.rem(other: Vector) = copy(x % other.x, y % other.y, z % other.z)

operator fun Location.plus(other: Double) = copy(x + other, y + other, z + other)
operator fun Location.minus(other: Double) = copy(x - other, y - other, z - other)
operator fun Location.times(other: Double) = copy(x * other, y * other, z * other)
operator fun Location.div(other: Double) = copy(x / other, y / other, z / other)
operator fun Location.rem(other: Double) = copy(x % other, y % other, z % other)