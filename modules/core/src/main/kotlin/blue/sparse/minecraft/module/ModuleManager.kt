package blue.sparse.minecraft.module

import blue.sparse.minecraft.SparseMCAPIPlugin
import blue.sparse.minecraft.core.CoreModule

object ModuleManager {
	private val modules = LinkedHashSet<Module>()

	@JvmName("onPluginEnable")
	internal fun onPluginEnable() {
		modules.add(CoreModule)
		CoreModule.onEnable()

		for (value in ModuleType.values()) {
			if(value == ModuleType.CORE)
				continue
			if(value == ModuleType.SCRIPTING)
				continue
			val module = value.clazz.objectInstance!!
			modules.add(module)
			module.onEnable()
			SparseMCAPIPlugin.getPlugin().logger.info("Enabled module ${module.name}")
		}

		CoreModule.loadAndEnablePlugins()
	}

	@JvmName("onPluginDisable")
	internal fun onPluginDisable() {
		modules.forEach(Module::onDisable)
	}
}