package blue.sparse.minecraft.core.extensions

import blue.sparse.minecraft.SparseMCPlugin
import blue.sparse.minecraft.module.ModuleClassLoader
import blue.sparse.minecraft.plugin.SparsePluginClassLoader
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin

val Any.providingPlugin: Plugin?
	get() {
		val clazz = javaClass
		val loader = clazz.classLoader
		if (loader is ModuleClassLoader)
			return SparseMCPlugin.getPlugin()
		if (loader is SparsePluginClassLoader)
			return loader.plugin
		return JavaPlugin.getProvidingPlugin(javaClass)
	}