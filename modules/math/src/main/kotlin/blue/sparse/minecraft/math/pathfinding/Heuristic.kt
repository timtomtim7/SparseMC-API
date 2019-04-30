package blue.sparse.minecraft.math.pathfinding

import blue.sparse.math.sqrt
import blue.sparse.math.vector.floats.*

interface Heuristic {

	fun estimate(a: Vector3f, b: Vector3f): Float

	object Euclidean: Heuristic {
		override fun estimate(a: Vector3f, b: Vector3f) = distance(a, b)
	}

	object Manhattan: Heuristic {
		override fun estimate(a: Vector3f, b: Vector3f) = sum(abs(a - b))
	}

	open class Diagonal(val d1: Float, val d2: Float): Heuristic {
		override fun estimate(a: Vector3f, b: Vector3f): Float {
			val v = abs(a - b)
			return d1 * sum(v) + (d2 - 2 * d1) * min(v)
		}
	}

	object Chebyshev: Diagonal(1f, 1f)

	object Octile: Diagonal(1f, sqrt(2f))

}