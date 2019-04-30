package blue.sparse.minecraft.inventory.menu

import kotlin.math.*

data class Vector2i(val x: Int, val y: Int) {
	operator fun plus(other: Vector2i) = Vector2i(x + other.x, y + other.y)
	operator fun plus(other: Int) = Vector2i(x + other, y + other)
	operator fun minus(other: Vector2i) = Vector2i(x - other.x, y - other.y)
	operator fun times(other: Vector2i) = Vector2i(x * other.x, y * other.y)
	operator fun unaryMinus() = Vector2i(-x, -y)

	fun abs() = Vector2i(abs(x), abs(y))
	fun max(max: Int) = Vector2i(max(x, max), max(y, max))
	fun min(max: Int) = Vector2i(min(x, max), min(y, max))
}