package blue.sparse.minecraft.scripting.server

import blue.sparse.minecraft.scripting.MinecraftScriptDependencies
import org.bukkit.Server
import org.bukkit.plugin.Plugin
import kotlin.script.templates.ScriptTemplateDefinition

@ScriptTemplateDefinition(resolver = MinecraftScriptDependencies::class)
abstract class ServerScriptTemplate(val server: Server, val plugin: Plugin)