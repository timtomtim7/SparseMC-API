package blue.sparse.minecraft.math.pathfinding

import blue.sparse.minecraft.math.pathfinding.ability.Abilities
import blue.sparse.minecraft.math.pathfinding.ability.Ability

class Pathfinder {

	val abilities = Abilities()
	var heuristic: Heuristic = Heuristic.Octile
	var directions = MovementDirections.all

	infix fun can(ability: Ability) = ability in abilities

	companion object {
		inline operator fun invoke(body: Pathfinder.() -> Unit): Pathfinder {
			return Pathfinder().apply(body)
		}
	}

	inner class Node(
			var parent: Node?,
			val x: Float,
			val y: Float,
			val z: Float
	) {

		val f: Float get() = h + g
		val h: Float get() = 0f
		val g: Float get() = 0f

	}

}