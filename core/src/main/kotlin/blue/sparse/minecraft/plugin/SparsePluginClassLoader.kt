package blue.sparse.minecraft.plugin

import blue.sparse.minecraft.core.extensions.server
import org.bukkit.plugin.InvalidPluginException
import java.io.File
import java.net.URLClassLoader
import java.util.jar.JarFile
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf

internal class SparsePluginClassLoader(
		val pluginLoader: SparsePluginLoader,
		parent: ClassLoader,
		val file: File
) : URLClassLoader(arrayOf(file.toURI().toURL()), parent) {

	val plugin: SparsePlugin

	init {
		var instance: SparsePlugin? = null

		val classNames = getClassNames(file).filter { '$' !in it && "Plugin" in it }
		for (className in classNames) {
			val clazz = findClass(className).kotlin
			if (!clazz.isSubclassOf(SparsePlugin::class))
				continue

			clazz.findAnnotation<PluginDescription>() ?: continue
			instance = clazz.objectInstance as SparsePlugin
			break
		}

		if (instance == null)
			throw InvalidPluginException("Plugin did not contain main class")

		instance.init(
				File(file.parentFile, instance.name),
				pluginLoader,
				this,
				server
		)
		plugin = instance
	}

	companion object {
		private fun getClassNames(file: File): List<String> {
			val names = JarFile(file).use {
				it.entries().asSequence()
						.filter { !it.isDirectory }
						.map { it.name }
						.toList().asSequence()
			}

			return names
					.filter { it.endsWith(".class") }
					.map { it.removeSuffix(".class").replace('/', '.') }
					.toList()
		}
	}

}