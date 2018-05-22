package blue.sparse.minecraft.plugin

annotation class PluginDescription(
		val name: String,
		val version: String,
		val softDepend: Array<String> = emptyArray(),
		val depend: Array<String> = emptyArray(),
		val loadBefore: Array<String> = emptyArray()
)