package blue.sparse.minecraft.core.extensions

import org.bukkit.*
import java.util.UUID

val server: Server
	get() = Bukkit.getServer()

fun Server.broadcastMessage(vararg parts: Any) {
	broadcastMessage(parts.joinToString(""))
}

fun Server.getExistingOfflinePlayer(name: String): OfflinePlayer? {
	return getOfflinePlayer(name).takeIf { it.isOnline || it.hasPlayedBefore() }
}

fun Server.getExistingOfflinePlayer(uuid: UUID): OfflinePlayer? {
	return getOfflinePlayer(uuid).takeIf { it.isOnline || it.hasPlayedBefore() }
}