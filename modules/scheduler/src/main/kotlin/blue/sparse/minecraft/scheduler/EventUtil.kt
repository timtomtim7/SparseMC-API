package blue.sparse.minecraft.scheduler

import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.plugin.IllegalPluginAccessException

internal fun getHandlerList(clazz: Class<out Event>): HandlerList {
	return getHandlerListNullable(clazz)
			?: throw IllegalPluginAccessException("${clazz.name} does not have a handler list.")
}

internal fun getHandlerListNullable(clazz: Class<out Event>): HandlerList? {
	return try {
		val method = getRegistrationClass(clazz).getDeclaredMethod("getHandlerList")
		method.isAccessible = true
		method.invoke(null) as HandlerList
	} catch (e: Exception) {
		null
	}
}

private fun getRegistrationClass(clazz: Class<out Event>): Class<out Event> {
	return try {
		clazz.getDeclaredMethod("getHandlerList")
		clazz
	} catch (e: NoSuchMethodException) {
		if (clazz.superclass != null && clazz.superclass != Event::class.java && Event::class.java.isAssignableFrom(clazz.superclass)) {
			getRegistrationClass(clazz.superclass.asSubclass(Event::class.java))
		} else throw IllegalPluginAccessException("Unable to find handler list for event " + clazz.name + ". Static getHandlerList method required!")
	}
}