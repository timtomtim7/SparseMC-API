package blue.sparse.minecraft.plugin

annotation class PluginDescription(
		val name: String,
		val version: String,
		val softDepend: Array<String> = [],
		val depend: Array<String> = [],
		val loadBefore: Array<String> = []
)