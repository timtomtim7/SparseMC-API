package blue.sparse.minecraft.scripting.chat

import blue.sparse.minecraft.scripting.MinecraftScriptDependencies
import org.bukkit.entity.Player
import kotlin.script.templates.ScriptTemplateDefinition

@ScriptTemplateDefinition(resolver = MinecraftScriptDependencies::class)
abstract class ChatScriptTemplate(val me: Player)