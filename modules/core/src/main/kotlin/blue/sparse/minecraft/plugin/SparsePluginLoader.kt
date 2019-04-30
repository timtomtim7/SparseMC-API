package blue.sparse.minecraft.plugin

import blue.sparse.minecraft.SparseMCAPIPlugin
import blue.sparse.minecraft.util.castDeclaredField
import org.bukkit.Server
import org.bukkit.event.Event
import org.bukkit.event.Listener
import org.bukkit.plugin.*
import java.io.File
import java.util.regex.Pattern

class SparsePluginLoader(val server: Server) : PluginLoader {

	private val loadedPlugins = HashMap<File, SparsePlugin>()

	init {
		instance = this
	}

	fun reloadPlugin(plugin: SparsePlugin): SparsePlugin {
		val file = (plugin.javaClass.classLoader as SparsePluginClassLoader).file
		val pluginManager = server.pluginManager

		pluginManager.disablePlugin(plugin)
		unloadPlugin(plugin)

		val newPlugin = pluginManager.loadPlugin(file)
		pluginManager.enablePlugin(newPlugin)

		return newPlugin as SparsePlugin
	}

	override fun loadPlugin(file: File): SparsePlugin {
		if (file.extension != "spl")
			throw IllegalArgumentException("Invalid file extension for SparsePluginLoader")

		val classLoader = SparsePluginClassLoader(this, javaClass.classLoader, file)
		val plugin = classLoader.plugin
		plugin.logger.info("Loading ${plugin.name} v${plugin.description.version}")
		plugin.onLoad()

		loadedPlugins[file] = plugin

		return plugin
	}

	fun unloadPlugin(plugin: SparsePlugin) {
		plugin.logger.info("Unloading ${plugin.name} v${plugin.description.version}")
		server.pluginManager.castDeclaredField<MutableList<Plugin>>("plugins").remove(plugin)
		server.pluginManager.castDeclaredField<MutableMap<String, Plugin>>("lookupNames").values.remove(plugin)
		loadedPlugins.values.remove(plugin)
		(plugin.javaClass.classLoader as SparsePluginClassLoader).close()
	}

	fun unloadAll() {
		loadedPlugins.values.toTypedArray().forEach(this::unloadPlugin)
	}

	fun disableAll() {
		loadedPlugins.values.forEach(this::disablePlugin)
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
//		val result = HashMap<Class<out Event>, MutableSet<RegisteredListener>>()
//
//		val eventFunctions = listener.javaClass.kotlin.functions.filter {
//			(it.parameters.size == 1 && it.parameters.first().type.jvmErasure.isSubclassOf(Event::class))
//		}.mapNotNull {
//			it to (it.findAnnotation<EventHandler>() ?: return@mapNotNull null)
//		}

		return SparseMCAPIPlugin.getPlugin().pluginLoader.createRegisteredListeners(listener, plugin)

//		TODO("Not implemented")
	}

	companion object {
		var instance: SparsePluginLoader? = null
			private set

	}

}