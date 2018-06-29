package blue.sparse.minecraft.scripting

import blue.sparse.maven.DependencyManager
import blue.sparse.maven.MavenArtifact
import blue.sparse.minecraft.SparseMCAPIPlugin
import blue.sparse.minecraft.module.*

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

		ChatScriptListener
	}
}