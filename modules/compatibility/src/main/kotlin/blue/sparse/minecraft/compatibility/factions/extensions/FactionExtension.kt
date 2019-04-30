package blue.sparse.minecraft.compatibility.factions.extensions

import com.massivecraft.factions.Faction
import org.bukkit.Chunk
import org.bukkit.OfflinePlayer

operator fun Faction.contains(player: OfflinePlayer) = fPlayers.contains(player.fPlayer)
operator fun Faction.contains(chunk: Chunk) = chunk claimedBy this