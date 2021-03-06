package blue.sparse.minecraft.core.util

import blue.sparse.minecraft.core.extensions.server

object PluginClasses {
	fun forName(name: String): Class<*> {
		try {
			return Class.forName(name)
		}catch(ignored: Throwable) { }

		return server.pluginManager.plugins.mapNotNull {
			try {
				Class.forName(name, true, it.javaClass.classLoader)
			}catch(ignored: Throwable) {
				null
			}
		}.firstOrNull() ?: throw ClassNotFoundException(name)
	}
}