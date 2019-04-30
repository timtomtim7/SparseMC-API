package blue.sparse.minecraft.commands.parsing.impl

import blue.sparse.minecraft.commands.parsing.Parser
import blue.sparse.minecraft.core.extensions.getExistingOfflinePlayer
import blue.sparse.minecraft.core.extensions.server
import java.util.UUID

val playerParser = Parser.of({ it.isLetterOrDigit() || it in "_-" }) {
	server.getPlayer(it) ?: server.getPlayer(UUID.fromString(it))
}

val offlinePlayerParser = Parser.of(
		{ it.isLetterOrDigit() || it in "_-" },
		{
			server.getExistingOfflinePlayer(it)
					?: server.getExistingOfflinePlayer(UUID.fromString(it))!!
		}
)
//val playerParser = Parser.ofString(server::getPlayer)
//val offlinePlayerParser = Parser.ofString { server.getOfflinePlayer(it).takeIf { it.isOnline || it.hasPlayedBefore() }!! }