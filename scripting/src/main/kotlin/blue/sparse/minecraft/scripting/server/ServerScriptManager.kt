package blue.sparse.minecraft.scripting.server

import blue.sparse.minecraft.SparseMCAPIPlugin
import blue.sparse.minecraft.core.extensions.server
import blue.sparse.minecraft.scripting.kotlin.KotlinCompilerManager
import java.io.File
import java.net.URLClassLoader
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.staticFunctions

object ServerScriptManager {

	val manager = KotlinCompilerManager.withMinecraftDependencies(URLClassLoader(emptyArray(), javaClass.classLoader))

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
				val clazz = manager.compileScript(file, ServerScriptTemplate::class)
				clazz.primaryConstructor?.call(server, SparseMCAPIPlugin.getPlugin())
			}
		}
	}

}