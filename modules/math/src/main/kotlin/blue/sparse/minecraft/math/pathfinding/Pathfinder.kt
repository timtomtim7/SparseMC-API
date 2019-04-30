package blue.sparse.minecraft.math.pathfinding

import blue.sparse.math.vector.floats.Vector3f
import blue.sparse.minecraft.math.extensions.toBlock
import blue.sparse.minecraft.math.pathfinding.ability.Abilities
import blue.sparse.minecraft.math.pathfinding.ability.Ability
import org.bukkit.World
import org.bukkit.block.Block

class Pathfinder {

	val abilities = Abilities()
	var heuristic: Heuristic = Heuristic.Octile
	var directions = MovementDirections.all

	infix fun can(ability: Ability) = ability in abilities

	fun findImmediately(world: World, startPosition: Vector3f, goalPosition: Vector3f) {
		val start = Node(null, startPosition)
		val goal = Node(null, goalPosition)

		val context = Context(world, start, goal)
		start.context = context
		goal.context = context
	}

	companion object {
		inline operator fun invoke(body: Pathfinder.() -> Unit): Pathfinder {
			return Pathfinder().apply(body)
		}
	}

	data class Context(
			val world: World,
			val start: Node,
			val goal: Node
	)

	inner class Node(
			var parent: Node?,
			val position: Vector3f
	) {

		lateinit var context: Context

		val open: Boolean
			get() = !block.type.isSolid

		val block: Block
			get() = position.toBlock(context.world)

		val neighbors: List<Node>
			get() = directions.offsets.map {
				Node(this, it + position, context)
			}.filter(Node::open)

		val f: Float get() = h + g

		val h: Float get() {
			val goal = context.goal
			return heuristic.estimate(position, goal.position)
		}

		val g: Float get() {
			val parent = parent ?: return 0f
			return parent.g + heuristic.estimate(parent.position, position)
		}

		constructor(parent: Node?, position: Vector3f, context: Context): this(parent, position) {
			this.context = context
		}

	}

}