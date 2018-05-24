package blue.sparse.minecraft.util

import java.lang.reflect.Field
import java.lang.reflect.Method

class Reflection(val value: Any) {

	val valueClass = value.javaClass

	operator fun get(name: String): ReflectionObject {
		return ReflectionObject(this, name)
	}

	class ReflectionObject(val origin: Reflection, val name: String) {

		val field: Field? by lazy { tryOrNull { origin.valueClass.getField(name) } }
		val declaredField: Field? by lazy { tryOrNull { origin.valueClass.getDeclaredField(name) } }

		var fieldValue: Any?
			get() {
				val f = this.field!!
				f.isAccessible = true
				return f.get(origin.value)
			}
			set(value) {
				val f = this.field!!
				f.isAccessible = true
				f.set(origin.value, value)
			}

		fun method(name: String, vararg params: Class<*>): Method? {
			return tryOrNull { origin.valueClass.getMethod(name, *params) }
		}

	}
}

inline fun <T> tryOrNull(body: () -> T): T? {
	return try {
		body()
	} catch (t: Throwable) {
		null
	}
}

val Any.reflection get() = Reflection(this)