package blue.sparse.minecraft.commands.extensions

import blue.sparse.minecraft.commands.CommandsModule
import blue.sparse.minecraft.core.PluginProvided

fun PluginProvided<*>.registerCommands(commands: Any) {
	CommandsModule.registerCommands(plugin, commands)
}