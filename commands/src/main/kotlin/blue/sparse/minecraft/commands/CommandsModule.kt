package blue.sparse.minecraft.commands

import blue.sparse.minecraft.core.extensions.server
import blue.sparse.minecraft.module.Module
import blue.sparse.minecraft.module.ModuleDefinition
import blue.sparse.minecraft.util.getAndCastDeclaredFieldValue
import org.bukkit.command.Command
import org.bukkit.command.SimpleCommandMap

@ModuleDefinition
object CommandsModule : Module {

	private val commandMap: SimpleCommandMap = server.getAndCastDeclaredFieldValue("commandMap")
	private val knownCommands: MutableMap<String, Command> = commandMap.getAndCastDeclaredFieldValue("knownCommands")

	override fun onEnable() {

	}

}