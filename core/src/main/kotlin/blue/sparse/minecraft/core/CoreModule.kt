package blue.sparse.minecraft.core

import blue.sparse.minecraft.core.extensions.server
import blue.sparse.minecraft.module.Module
import blue.sparse.minecraft.module.ModuleDefinition
import blue.sparse.minecraft.plugin.SparsePluginLoader
import blue.sparse.minecraft.util.reflection
import java.io.File

@ModuleDefinition
object CoreModule : Module {

	override fun onEnable() {
		registerPluginLoader()
		loadPlugins()
	}

	override fun onDisable() {
		unloadPlugins()
		unregisterPluginLoader()
	}

	private fun registerPluginLoader() {
		server.pluginManager.registerInterface(SparsePluginLoader::class.java)
	}

	private fun unregisterPluginLoader() {
		val pm = server.pluginManager
		val fileAssoc = pm.reflection["fileAssociations"].fieldValue as MutableMap<*, *>
		fileAssoc.values.removeAll { it?.javaClass == SparsePluginLoader::class.java }
	}

	private fun loadPlugins() {
		val pluginsFolder = File("plugins")
		if (pluginsFolder.exists()) {
			val files = pluginsFolder.listFiles { f -> f.extension == "spl" }
			val plugins = files.mapNotNull {
				try {
					server.pluginManager.loadPlugin(it)
				} catch (t: Throwable) {
					logger.severe("Error loading plugin \"${it.name}\":")
					t.printStackTrace()
					null
				}
			}

			//TODO: Sort by dependencies,
			// download and load maven dependencies,
			// download plugin dependencies (if available)

			plugins.forEach(server.pluginManager::enablePlugin)
		}
	}

	private fun unloadPlugins() {
		SparsePluginLoader.instance?.let {
			it.disableAll()
			it.unloadAll()
		}
	}

}