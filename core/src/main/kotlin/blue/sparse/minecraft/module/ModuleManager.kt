package blue.sparse.minecraft.module

import blue.sparse.minecraft.SparseMCAPIPlugin
import blue.sparse.minecraft.core.CoreModule
import java.io.File
import java.io.FileFilter

object ModuleManager {

	private val logger = SparseMCAPIPlugin.getPlugin().logger

	private val modules = LinkedHashSet<Module>()

	@JvmName("onPluginEnable")
	internal fun onPluginEnable() {
		modules.add(CoreModule)
		loadModules()

		modules.forEach(Module::onEnable)
	}

	@JvmName("onPluginDisable")
	internal fun onPluginDisable() {
		modules.forEach(Module::onDisable)
	}

	private fun loadModules() {
		val folder = File(SparseMCAPIPlugin.getPlugin().dataFolder, "modules")
		if (!folder.exists() || !folder.isDirectory)
			return

		folder.listFiles(FileFilter { it.extension == "jar" }).forEach {
			val module = loadModule(it)
			modules.add(module)
			logger.info("Module loaded: ${module.name}")
		}
	}

	private fun loadModule(file: File): Module {
		return ModuleClassLoader(file, javaClass.classLoader).module
	}


}