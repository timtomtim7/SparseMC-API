package blue.sparse.minecraft.scripting

import org.bukkit.entity.Player
import kotlin.script.templates.ScriptTemplateDefinition

@ScriptTemplateDefinition
abstract class ChatScriptTemplate(val me: Player)