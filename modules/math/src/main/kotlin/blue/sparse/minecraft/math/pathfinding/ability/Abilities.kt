package blue.sparse.minecraft.math.pathfinding.ability

import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

class Abilities private constructor(
		private val abilities: MutableSet<Ability>
): Set<Ability> by abilities {

	constructor(): this(LinkedHashSet())

	init {
		add<Ability>()
	}

	fun remove(ability: Ability) = abilities.remove(ability)
	inline fun <reified T : Ability> remove() {
		useSubAbilities(T::class, false) { result, it ->
			remove(it) || result
		}
	}

	fun add(ability: Ability) = abilities.add(ability)

	inline fun <reified T : Ability> add() {
		useSubAbilities(T::class, false) { result, it ->
			add(it) || result
		}
	}

	@PublishedApi
	internal fun <R> useSubAbilities(clazz: KClass<*>, default: R, body: (R, Ability) -> R): R {
		val inst = clazz.objectInstance
		if (inst != null)
			return body(default, inst as Ability)

		return clazz.nestedClasses
				.filter { it.isSubclassOf(Ability::class) }
				.fold(default) { result, it -> useSubAbilities(it, result, body) }
	}

	override fun toString(): String {
		return "Abilities($abilities)"
	}

}