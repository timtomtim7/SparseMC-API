package blue.sparse.minecraft.scripting.server

import blue.sparse.minecraft.SparseMCAPIPlugin
import blue.sparse.minecraft.scripting.kotlin.KotlinCompilerManager

object ServerScriptManager {

	private val pluginJars = SparseMCAPIPlugin.getPlugin()
			.dataFolder.parentFile.listFiles().filter { it.extension == "jar" }

	private val moduleJars = SparseMCAPIPlugin.getModulesFolder()
			.listFiles().filter { it.extension == "jar" }

	private val dependencyJars = SparseMCAPIPlugin.getDependenciesFolder()
			.listFiles().filter { it.extension == "jar" }

	val manager = KotlinCompilerManager(pluginJars + moduleJars + dependencyJars)



}