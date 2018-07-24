package blue.sparse.minecraft.scripting

import blue.sparse.data.config.file.FileConfig
import blue.sparse.maven.DependencyManager
import blue.sparse.maven.MavenArtifact
import blue.sparse.minecraft.SparseMCAPIPlugin
import blue.sparse.minecraft.module.*
import blue.sparse.minecraft.scripting.chat.ChatScriptListener
import java.io.File

@ModuleDefinition
object ScriptingModule : Module {

	override val type = ModuleType.SCRIPTING

	override fun onEnable() {
		val compilerEmbeddable = MavenArtifact("org.jetbrains.kotlin", "kotlin-compiler-embeddable")
		val scriptRuntime = MavenArtifact("org.jetbrains.kotlin", "kotlin-script-runtime")

		val folder = SparseMCAPIPlugin.getDependenciesFolder()
		DependencyManager.updateAndLoadDependencies(
				listOf(scriptRuntime, compilerEmbeddable),
				folder
		)

		Config.saveDefaults()
		ChatScriptListener
	}

	object Config: FileConfig(File(plugin.dataFolder, "scripting.cfg")) {
		val test by 5

		override fun afterReload() {
			println("test = $test")
		}
//		val enableChatScripting by true
//		val chatScriptDefaultsToOperators by false

//		override fun afterReload() {
//			super.afterReload()
//		}
	}
}