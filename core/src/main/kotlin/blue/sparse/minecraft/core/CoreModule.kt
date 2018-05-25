package blue.sparse.minecraft.core

import blue.sparse.minecraft.core.extensions.server
import blue.sparse.minecraft.module.Module
import blue.sparse.minecraft.module.ModuleDefinition
import blue.sparse.minecraft.plugin.SparsePluginLoader
import blue.sparse.minecraft.util.castDeclaredField
import blue.sparse.minecraft.util.reflection
import org.bukkit.plugin.PluginLoader
import java.io.File

@ModuleDefinition
object CoreModule : Module {

	override fun onEnable() {
		registerPluginLoader()
		loadAndEnablePlugins()
	}

	override fun onDisable() {
		disableAndUnloadPlugins()
		unregisterPluginLoader()
	}

	private fun registerPluginLoader() {
		server.pluginManager.registerInterface(SparsePluginLoader::class.java)
	}

	private fun unregisterPluginLoader() {
		val pm = server.pluginManager
//		val fileAssociations = pm.castDeclaredField<MutableMap<*, PluginLoader>>("fileAssociations")
		val fileAssoc = pm.reflection["fileAssociations"].declaredFieldValue as MutableMap<*, *>
		fileAssoc.values.removeAll { it?.javaClass == SparsePluginLoader::class.java }
	}

	private fun loadAndEnablePlugins() {
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

			//TODO: download plugin dependencies (if available)
			//TODO: sort by dependencies
			//TODO: download maven dependencies

			plugins.forEach(server.pluginManager::enablePlugin)
		}
	}

	private fun disableAndUnloadPlugins() {
		SparsePluginLoader.instance?.let {
			it.disableAll()
			it.unloadAll()
		}
	}

}