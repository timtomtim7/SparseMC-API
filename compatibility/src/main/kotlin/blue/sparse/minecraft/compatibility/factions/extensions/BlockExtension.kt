package blue.sparse.minecraft.compatibility.factions.extensions

import com.massivecraft.factions.Faction
import org.bukkit.OfflinePlayer
import org.bukkit.block.Block

val Block.fLocation
	get() = location.fLocation

infix fun Block.claimedBy(faction: Faction) = chunk claimedBy faction
infix fun Block.notClaimedBy(faction: Faction) = !(chunk claimedBy faction)

infix fun Block.claimedBy(player: OfflinePlayer) = chunk claimedBy player.faction
infix fun Block.notClaimedBy(player: OfflinePlayer) = chunk notClaimedBy player.faction