package blue.sparse.minecraft.scripting.kotlin.old

import java.lang.reflect.Constructor
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
class CompiledScript<T : Any> internal constructor(val clazz: KClass<T>, val time: Long = System.currentTimeMillis()) {

	private val constructor = clazz.java.constructors.first() as Constructor<T>
	private val resultField = clazz.java.getDeclaredField("\$\$result")

	operator fun invoke(vararg args: Any?): Result {
		val inst = constructor.newInstance(*args) as T
		val result = resultField.get(inst)
		return Result(inst, result)
	}

	inner class Result internal constructor(val instance: T, val result: Any?) {
		val script = this@CompiledScript
	}
}