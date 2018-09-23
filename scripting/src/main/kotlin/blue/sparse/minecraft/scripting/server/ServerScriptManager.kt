package blue.sparse.minecraft.scripting.server

import blue.sparse.minecraft.SparseMCAPIPlugin
import blue.sparse.minecraft.core.extensions.server
import blue.sparse.minecraft.scripting.kotlin.KotlinCompilerManager
import java.io.File
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.staticFunctions

object ServerScriptManager {

	private val pluginJars = SparseMCAPIPlugin.getPlugin()
			.dataFolder.parentFile.listFiles().filter { it.extension == "jar" }

	private val moduleJars = SparseMCAPIPlugin.getModulesFolder()
			.listFiles().filter { it.extension == "jar" }

	private val dependencyJars = SparseMCAPIPlugin.getDependenciesFolder()
			.listFiles().filter { it.extension == "jar" }

	val manager = KotlinCompilerManager.withMinecraftDependencies(ClassLoader.getSystemClassLoader())

//	val manager = KotlinCompilerManager(
//			pluginJars + moduleJars + dependencyJars,
//			javaClass.classLoader
//	)


	val folder = File("scripts/auto")

	fun loadAllScripts() {
		if (!folder.exists())
			return

		folder.listFiles().forEach {
			try {
				executeScript(it)
			} catch(t: Throwable) {
				println(it)
				t.printStackTrace()
			}
		}
	}

	fun executeScript(file: File) {
		when {
			file.extension == "kt" -> {
				val clazz = manager.compileStandard(file) ?: return
				clazz.staticFunctions.find { it.name == "start" }?.call()
			}
			file.extension == "kts" -> {
				val clazz = manager.compileScript(file)
				clazz.primaryConstructor?.call(server, SparseMCAPIPlugin.getPlugin())
			}
		}
	}

}