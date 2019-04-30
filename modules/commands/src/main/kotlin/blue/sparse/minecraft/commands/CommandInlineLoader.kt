package blue.sparse.minecraft.commands

import kotlin.reflect.KClass
import kotlin.reflect.full.declaredFunctions

object CommandInlineLoader {
	inline fun <reified T1> command(noinline body: (T1) -> Unit) {
		command(body, T1::class)
	}

	fun command(function: Function<Unit>, vararg parameterTypes: KClass<*>) {
		val invoke = function::class.declaredFunctions.first { it.name == "invoke" }
		invoke.parameters.forEach {
			println(it.type)
		}
	}
}