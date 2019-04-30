package blue.sparse.minecraft.math.pathfinding

import blue.sparse.math.vector.floats.Vector3f
import blue.sparse.math.vector.floats.vec3f

class MovementDirections(val offsets: List<Vector3f>) {
	constructor(vararg offsets: Vector3f): this(offsets.toList())

	operator fun plus(other: MovementDirections): MovementDirections {
		return MovementDirections(offsets + other.offsets)
	}

	companion object {

		val vertical = MovementDirections(
				vec3f(0f, 1f, 0f),
				vec3f(0f, -1f, 0f)
		)

		val cardinal = MovementDirections(
				vec3f(1f, 0f, 0f),
				vec3f(-1f, 0f, 0f),
				vec3f(0f, 0f, 1f),
				vec3f(0f, 0f, -1f)
		)

		val diagonal = MovementDirections(
				vec3f(1f, 0f, 1f),
				vec3f(-1f, 0f, 1f),
				vec3f(-1f, 0f, -1f),
				vec3f(1f, 0f, -1f)
		)

		val all = vertical + cardinal + diagonal

	}
}