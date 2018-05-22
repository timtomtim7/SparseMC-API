package blue.sparse.minecraft.module

import java.util.logging.Logger

interface Module {
	val name: String
		get() = javaClass.simpleName.removeSuffix("Module")

	val logger: Logger
		get() = Logger.getLogger("SparseMC-$name")

	fun onEnable() {}
	fun onDisable() {}
}