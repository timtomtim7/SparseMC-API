package blue.sparse.minecraft.plugin

import org.bukkit.event.*
import org.bukkit.plugin.*
import java.io.File
import java.util.regex.Pattern
import kotlin.reflect.full.*
import kotlin.reflect.jvm.jvmErasure

internal class SparsePluginLoader : PluginLoader {

	private val loadedPlugins = HashMap<File, SparsePlugin>()

	override fun loadPlugin(file: File): Plugin {
		if (file.extension != "spl")
			throw IllegalArgumentException("Invalid file extension for SparsePluginLoader")

		val classLoader = SparsePluginClassLoader(this, javaClass.classLoader, file)
		val plugin = classLoader.plugin
		plugin.logger.info("Loading ${plugin.name} v${plugin.description.version}")

		loadedPlugins[file] = plugin

		return plugin
	}

	override fun enablePlugin(plugin: Plugin) {
		if (plugin !is SparsePlugin)
			throw IllegalArgumentException("Plugin is not a SparsePlugin, it cannot be enabled by this PluginLoader")

		if (plugin.isEnabled) return
		plugin.logger.info("Enabling ${plugin.name} v${plugin.description.version}")
		plugin.isEnabled = true
	}

	override fun disablePlugin(plugin: Plugin) {
		if (plugin !is SparsePlugin)
			throw IllegalArgumentException("Plugin is not a SparsePlugin, it cannot be enabled by this PluginLoader")

		if (!plugin.isEnabled) return
		plugin.logger.info("Disabling ${plugin.name} v${plugin.description.version}")
		plugin.isEnabled = false
	}

	override fun getPluginDescription(file: File): PluginDescriptionFile? {
		return loadedPlugins[file]?.description
	}

	override fun getPluginFileFilters(): Array<Pattern> {
		return arrayOf(Pattern.compile("\\.spl$"))
	}

	override fun createRegisteredListeners(listener: Listener, plugin: Plugin): MutableMap<Class<out Event>, MutableSet<RegisteredListener>> {
		val result = HashMap<Class<out Event>, MutableSet<RegisteredListener>>()

		val eventFunctions = listener.javaClass.kotlin.functions.filter {
			(it.parameters.size == 1 && it.parameters.first().type.jvmErasure.isSubclassOf(Event::class))
		}.mapNotNull {
			it to (it.findAnnotation<EventHandler>() ?: return@mapNotNull null)
		}

		TODO("Not implemented")
	}

}