package blue.sparse.minecraft.commands.test

import blue.sparse.minecraft.commands.Execute
import blue.sparse.minecraft.core.extensions.server
import blue.sparse.minecraft.plugin.*
import org.bukkit.ChatColor

object TestCommands {

//	fun Execute.sparseReload(pluginName: String) {
//		if (!sender.hasPermission("sparsemcapi.reload.plugin"))
//			errorRaw(ChatColor.RED, "No permission to reload.")
//
//		val pluginManager = server.pluginManager
//		var plugin: SparsePlugin = (pluginManager.getPlugin(pluginName)
//				?: errorRaw(ChatColor.RED, "Plugin either doesn't exist or isn't loaded")) as? SparsePlugin
//				?: errorRaw(ChatColor.RED, "That plugin was not loaded by SparseMC-API")
//
//		val file = (plugin.javaClass.classLoader as SparsePluginClassLoader).file
//
//		val loader = SparsePluginLoader.instance!!
//		loader.disablePlugin(plugin)
//		loader.unloadPlugin(plugin)
//
//		plugin = pluginManager.loadPlugin(file) as SparsePlugin
//		pluginManager.enablePlugin(plugin)
//
//		replyRaw(ChatColor.GRAY, "Plugin ", ChatColor.GREEN, plugin.name, ChatColor.GRAY, " reloaded.")
//	}

//	@Command.Description("Message another player or players.")
//	fun Execute.msg(target: Either<Player, List<Player>>, message: SpacedString) {
//		val targets = target.fold(::listOf, { it })
//
//		targets.forEach {
//			it.sendMessage(plugin, "privateMessageReceived", "sender" to sender.name, "message" to message)
//		}
//
//		reply("privateMessageSent", "receivers" to targets.joinToString { it.name }, "message" to message)
//	}

//	@Command.Aliases("i", "item")
//	fun Execute.give(target: Player? = sender as? Player, itemType: Material, amount: Int = 1) {
//		if (target == null)
//			errorRaw(ChatColor.RED, "Expected target")
//
//		replyRaw(ChatColor.GRAY, "You were given ", ChatColor.GREEN, amount, 'x', ' ', itemType)
//		target.inventory.addItem(ItemStack(itemType, amount))
//	}
//
//	@Command.Aliases("gm")
//	fun Execute.gamemode(target: Player? = sender as? Player, gameMode: GameMode) {
//		if (target == null)
//			errorRaw(ChatColor.RED, "Expected target")
//
//		target.gameMode = gameMode
//	}
}