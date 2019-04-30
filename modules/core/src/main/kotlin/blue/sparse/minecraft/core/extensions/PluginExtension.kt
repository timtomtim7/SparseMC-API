package blue.sparse.minecraft.core.extensions

import blue.sparse.minecraft.plugin.SparsePluginClassLoader
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import kotlin.reflect.KClass

val Any.providingPlugin: Plugin?
	get() = javaClass.providingPlugin

val KClass<*>.providingPlugin: Plugin?
	get() = java.providingPlugin

val Class<*>.providingPlugin: Plugin?
	get() {
		val loader = classLoader
//		if (loader is ModuleClassLoader)
//			return SparseMCAPIPlugin.getPlugin()
		if (loader is SparsePluginClassLoader)
			return loader.plugin
		return JavaPlugin.getProvidingPlugin(javaClass)
	}
