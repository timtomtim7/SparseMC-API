package blue.sparse.minecraft.core.extensions

import org.bukkit.command.CommandSender

fun CommandSender.sendMessage(vararg parts: Any) {
	sendMessage(parts.joinToString(""))
}

