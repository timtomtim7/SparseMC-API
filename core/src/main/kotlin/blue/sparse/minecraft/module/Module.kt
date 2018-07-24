package blue.sparse.minecraft.module

import blue.sparse.minecraft.SparseMCAPIPlugin
import blue.sparse.minecraft.core.PluginProvided
import java.util.logging.Logger

interface Module: PluginProvided<SparseMCAPIPlugin> {
	override val plugin: SparseMCAPIPlugin
		get() = SparseMCAPIPlugin.getPlugin()

	val name: String
		get() = javaClass.simpleName.removeSuffix("Module")

	val logger: Logger
		get() = Logger.getLogger("SparseMC-$name")

	val type: ModuleType

	fun onEnable() {}
	fun onDisable() {}
}