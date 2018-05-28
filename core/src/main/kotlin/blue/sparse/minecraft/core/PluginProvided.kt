package blue.sparse.minecraft.core

import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin

interface PluginProvided<T : Plugin> {
	val plugin: T
		get() {
			val clazz = javaClass
			val loader = clazz.classLoader
//			if(loader is ModuleClassLoader)
//				return SparseMCAPIPlugin.getPlugin() as T
			return JavaPlugin.getProvidingPlugin(javaClass) as T
		}
}