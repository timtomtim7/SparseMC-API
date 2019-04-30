package blue.sparse.minecraft.math.pathfinding.ability

sealed class Ability {
	sealed class Doors: Ability() {
		object Regular: Doors()
		object Button: Doors()
		object Pressure: Doors()
	}

	sealed class Move: Ability() {
		object Walk: Move()
		object Sprint: Move()
		object Gravity: Move()

		sealed class Climb: Ability() {
			object Ladders: Climb()
			object Vines: Climb()
		}

		sealed class Swim: Ability() {
			object Water: Swim()
			object Lava: Swim()
		}
	}

	override fun toString(): String {
		return javaClass.name
				.removePrefix(Ability::class.java.name)
				.replace("$", "")
	}
}