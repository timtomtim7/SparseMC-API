package blue.sparse.minecraft.commands

import blue.sparse.minecraft.SparseMCPlugin
import blue.sparse.minecraft.commands.test.TestCommands
import blue.sparse.minecraft.core.extensions.server
import blue.sparse.minecraft.module.Module
import blue.sparse.minecraft.module.ModuleDefinition
import blue.sparse.minecraft.util.castDeclaredField
import org.bukkit.command.Command
import org.bukkit.command.SimpleCommandMap
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.PluginDisableEvent
import org.bukkit.plugin.Plugin
import kotlin.reflect.KClass

@ModuleDefinition
object CommandsModule : Module, Listener {

	private val commandMap: SimpleCommandMap = server.castDeclaredField("commandMap")
	private val knownCommands: MutableMap<String, Command> = commandMap.castDeclaredField("knownCommands")
	private val pluginCommands = HashMap<Plugin, MutableSet<Command>>()

	override fun onEnable() {
		server.pluginManager.registerEvents(this, SparseMCPlugin.getPlugin())
		registerCommands(SparseMCPlugin.getPlugin(), TestCommands)
	}

	override fun onDisable() {

	}

	@EventHandler
	fun PluginDisableEvent.onPluginDisable() {
		knownCommands.values.removeAll(pluginCommands.remove(plugin) ?: return)
	}

	fun registerCommands(plugin: Plugin, obj: Any) {
		registerCommands(plugin, obj.javaClass.kotlin)
	}

	fun registerCommands(plugin: Plugin, clazz: KClass<*>) {
		CommandReflectionLoader.scan(plugin, clazz).forEach {
			registerCommand(plugin, it)
		}
	}

	private fun registerCommand(plugin: Plugin, command: Command) {
		if (pluginCommands.getOrPut(plugin, ::HashSet).add(command)) {
			commandMap.register(plugin.name.toLowerCase(), command)
		}
	}
}