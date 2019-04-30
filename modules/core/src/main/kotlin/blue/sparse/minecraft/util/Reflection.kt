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
			get() = getFieldValue(this.field!!)
			set(value) = setFieldValue(this.field!!, value)

		var declaredFieldValue: Any?
			get() = getFieldValue(this.declaredField!!)
			set(value) = setFieldValue(this.declaredField!!, value)

		private fun getFieldValue(field: Field): Any? {
			field.isAccessible = true
			return field[origin.value]
		}

		private fun setFieldValue(field: Field, value: Any?) {
			field.isAccessible = true
			field.set(origin.value, value)

		}

		fun method(vararg params: Class<*>): Method? {
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

inline fun <reified T: Throwable> ignore(body: () -> Unit) {
	try {
		body()
	} catch(t: Throwable) {
		if(t !is T)
			throw t
	}
}

//inline fun ignore(vararg exceptions: Class<out Throwable>, body: () -> Unit) {
//	try {
//		body()
//	} catch (t: Throwable) {
//	}
//}

val Any.reflection get() = Reflection(this)