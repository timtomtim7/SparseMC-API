package blue.sparse.minecraft.compatibility.factions.extensions

import com.massivecraft.factions.FLocation
import com.massivecraft.factions.Faction
import org.bukkit.Location
import org.bukkit.OfflinePlayer

val Location.fLocation
	get() = FLocation(this)

infix fun Location.claimedBy(faction: Faction) = chunk claimedBy faction
infix fun Location.notClaimedBy(faction: Faction) = chunk notClaimedBy faction

infix fun Location.claimedBy(player: OfflinePlayer) = chunk claimedBy player.faction
infix fun Location.notClaimedBy(player: OfflinePlayer) = chunk notClaimedBy player.faction
