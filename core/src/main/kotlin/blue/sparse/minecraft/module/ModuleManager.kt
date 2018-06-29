package blue.sparse.minecraft.module

import blue.sparse.minecraft.SparseMCAPIPlugin
import blue.sparse.minecraft.core.CoreModule
import java.io.File
import java.io.FileFilter

object ModuleManager {

	const val MODULE_MAVEN_REPO = "https://sparse.blue/maven/"
	const val MODULE_MAVEN_GROUP = "blue.sparse.minecraft"

	val modulesFolder = File(SparseMCAPIPlugin.getPlugin().dataFolder, "modules")

	private val logger = SparseMCAPIPlugin.getPlugin().logger

	private val modules = LinkedHashSet<Module>()

	@JvmName("onPluginEnable")
	internal fun onPluginEnable() {
		modules.add(CoreModule)
		CoreModule.onEnable()
	}

	@JvmName("onPluginDisable")
	internal fun onPluginDisable() {
		modules.forEach(Module::onDisable)
	}

//	private fun loadModules() {
//		val folder = modulesFolder
//		if (!folder.exists() || !folder.isDirectory)
//			return
//
//		folder.listFiles(FileFilter { it.extension == "jar" }).forEach { file ->
//			val type = ModuleType.values().find {
//				file.nameWithoutExtension.startsWith(it.name, true)
//			}
//			if(type != null)
//				loadModule(type, file)
//		}
//	}

	internal fun loadModule(type: ModuleType, file: File): Module {
		modules.find { it.type == type }?.let { return it }
		logger.info("Loading module: ${type.name.toLowerCase()}")

		val module = ModuleClassLoader(file, javaClass.classLoader).module
		modules.add(module)
		logger.info("Module loaded: ${type.name.toLowerCase()}")

		module.onEnable()

		return module
	}


}