@file:Suppress("UNCHECKED_CAST")

package blue.sparse.minecraft.util

//fun getClassByName(name: String) = Class.forName(name)
//fun getClassByNameOrNull(name: String) =  try { getClassByName(name) } catch (e: Exception) { null }

//fun Class<*>.getAccessibleMethod(name: String, vararg parameterClasses: Class<*>): Method {
//	val method = getMethod(name, *parameterClasses)
//	method.isAccessible = true
//	return method
//}
//
//fun Class<*>.getAccessibleMethodOrNull(name: String, vararg parameterClasses: Class<*>): Method? {
//	return try { getAccessibleMethod(name, *parameterClasses) }
//	catch (e: Exception) { null }
//}

//inline fun <reified T> getStaticFieldValue(name: String): Any = T::class.java.getField(name).apply { isAccessible = true }.get(null)
//inline fun <reified T, V> getAndCastStaticFieldValue(name: String): V = getStaticFieldValue<T>(name) as V

fun Any.getDeclaredFieldValue(name: String): Any = javaClass.getDeclaredField(name).apply { isAccessible = true }.get(this)
fun <T> Any.castDeclaredField(name: String): T = getDeclaredFieldValue(name) as T

//fun Any.getFieldValue(name: String): Any = javaClass.getField(name).apply { isAccessible = true }.get(this)
fun Any.getFieldValue(name: String) = getFieldValue(javaClass, name)

private fun Any.getFieldValue(clazz: Class<*>, name: String): Any {
	return try {
		val field = clazz.getDeclaredField(name)
		field.isAccessible = true
		field.get(this)
	}catch(t: Throwable) {
		if(clazz.superclass == null)
			throw NoSuchFieldException(name)
		getFieldValue(clazz.superclass, name)
	}
}

fun <T> Any.castField(name: String): T = getFieldValue(name) as T
