package blue.sparse.minecraft.core

import blue.sparse.minecraft.SparseMCPlugin
import blue.sparse.minecraft.module.ModuleClassLoader
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin

interface PluginProvided<T: Plugin> {
	val plugin: T
		get() {
			val clazz = javaClass
			val loader = clazz.classLoader
			if(loader is ModuleClassLoader)
				return SparseMCPlugin.getPlugin() as T
			return JavaPlugin.getProvidingPlugin(javaClass) as T
		}
}