package blue.sparse.minecraft.core.extensions

import org.bukkit.Bukkit
import org.bukkit.Server

val server: Server
	get() = Bukkit.getServer()

fun Server.broadcastMessage(vararg parts: Any) {
	broadcastMessage(parts.joinToString(""))
}