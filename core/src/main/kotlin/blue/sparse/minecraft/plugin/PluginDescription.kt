package blue.sparse.minecraft.plugin

import blue.sparse.minecraft.module.ModuleType

annotation class PluginDescription(
		val name: String,
		val version: String,
		val modules: Array<ModuleType> = [],
		val softDepend: Array<String> = [],
		val depend: Array<String> = [],
		val loadBefore: Array<String> = []
)