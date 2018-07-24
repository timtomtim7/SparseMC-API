package blue.sparse.minecraft.scripting

import kotlin.script.dependencies.Environment
import kotlin.script.dependencies.ScriptContents
import kotlin.script.experimental.dependencies.*

object MinecraftScriptDependencies: DependenciesResolver {
	override fun resolve(scriptContents: ScriptContents, environment: Environment): DependenciesResolver.ResolveResult {
		return ScriptDependencies(
				imports = listOf(
						"org.bukkit.*",
						"org.bukkit.entity.*",
						"org.bukkit.block.*",
						"org.bukkit.event.*",
						"org.bukkit.inventory.*",
						"org.bukkit.inventory.meta.*",
						"blue.sparse.*",
						"blue.sparse.minecraft.*",
						"blue.sparse.minecraft.core.*"
				)
		).asSuccess()
	}
}