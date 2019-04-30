package blue.sparse.minecraft.scripting

import blue.sparse.data.config.file.FileConfig
import blue.sparse.maven.DependencyManager
import blue.sparse.maven.MavenArtifact
import blue.sparse.minecraft.SparseMCAPIPlugin
import blue.sparse.minecraft.module.Module
import blue.sparse.minecraft.module.ModuleType
import blue.sparse.minecraft.scripting.chat.ChatScriptListener
import java.io.File

object ScriptingModule : Module {

	override val type = ModuleType.SCRIPTING

	val cacheFolder: File
		get() = File(SparseMCAPIPlugin.dataFolder(), "scriptCache").apply { mkdirs() }

	override fun onEnable() {
		val compilerEmbeddable = MavenArtifact("org.jetbrains.kotlin", "kotlin-compiler")
		val scriptRuntime = MavenArtifact("org.jetbrains.kotlin", "kotlin-script-runtime")
		val trove4j = MavenArtifact("org.jetbrains.intellij.deps", "trove4j")

		val folder = SparseMCAPIPlugin.getDependenciesFolder()
		try {
			DependencyManager.updateAndLoadDependencies(
					listOf(scriptRuntime, compilerEmbeddable, trove4j),
					mapOf(
							compilerEmbeddable to "1.3.21",
							scriptRuntime to "1.3.21",
							trove4j to "1.0.20181211"
					),
					folder,
					DependencyManager.getHighestClassLoader()
			)
		} catch (t: Throwable) {
			DependencyManager.updateAndLoadDependencies(
					listOf(scriptRuntime, compilerEmbeddable, trove4j),
					folder
			)
		}

		ChatScriptListener
//		server.scheduler.scheduleSyncDelayedTask(plugin) {
//			ServerScriptManager.loadAllScripts()
//		}
	}

	object Config: FileConfig(File(plugin.dataFolder, "scripting.cfg")) {
		val enableChatScripting by true
		val chatScriptDefaultsToOperators by false
	}
}
