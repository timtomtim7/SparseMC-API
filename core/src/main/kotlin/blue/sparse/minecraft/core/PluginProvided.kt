package blue.sparse.minecraft.core

import blue.sparse.minecraft.core.extensions.getPluginLocale
import blue.sparse.minecraft.core.extensions.server
import blue.sparse.minecraft.core.i18n.PluginLocale
import blue.sparse.minecraft.plugin.SparsePluginClassLoader
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin

@Suppress("UNCHECKED_CAST")
interface PluginProvided<T : Plugin> {
	val plugin: T
		get() {
			val clazz = javaClass
			val loader = clazz.classLoader
			if(loader is SparsePluginClassLoader)
				return loader.plugin as T
			return JavaPlugin.getProvidingPlugin(javaClass) as T
		}

	fun registerListener(listener: Listener) {
		server.pluginManager.registerEvents(listener, plugin)
	}

	val Player.pluginLocale: PluginLocale
		get() = getPluginLocale(plugin)
}