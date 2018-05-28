package blue.sparse.minecraft.commands.test

import blue.sparse.minecraft.commands.Command
import blue.sparse.minecraft.commands.Execute
import blue.sparse.minecraft.commands.parsing.util.SpacedString
import blue.sparse.minecraft.core.extensions.sendMessage
import blue.sparse.minecraft.util.Either
import blue.sparse.minecraft.util.fold
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object TestCommands {

	@Command.Description("Message another player or players.")
	fun Execute.msg(target: Either<Player, List<Player>>, message: SpacedString) {
		val targets = target.fold(::listOf, { it })

		targets.forEach {
			it.sendMessage(plugin, "privateMessageReceived", "sender" to sender.name, "message" to message)
		}

		reply("privateMessageSent", "receivers" to targets.joinToString { it.name }, "message" to message)
	}

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