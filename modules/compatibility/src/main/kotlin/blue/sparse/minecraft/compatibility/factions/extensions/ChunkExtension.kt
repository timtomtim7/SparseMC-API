package blue.sparse.minecraft.compatibility.factions.extensions

import blue.sparse.minecraft.compatibility.factions.FactionsCompat
import com.massivecraft.factions.FLocation
import com.massivecraft.factions.Faction
import org.bukkit.Chunk
import org.bukkit.OfflinePlayer

val Chunk.fLocation: FLocation
	get() = FLocation(getBlock(0, 0, 0))

val Chunk.faction: Faction
	get() = FactionsCompat.claims.getFactionAt(fLocation)

infix fun Chunk.claimedBy(faction: Faction) = faction == faction
infix fun Chunk.notClaimedBy(faction: Faction) = faction != faction

infix fun Chunk.claimedBy(player: OfflinePlayer) = this claimedBy player.faction
infix fun Chunk.notClaimedBy(player: OfflinePlayer) = !(this claimedBy player.faction)
