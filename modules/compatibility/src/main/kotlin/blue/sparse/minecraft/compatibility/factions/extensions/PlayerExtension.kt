package blue.sparse.minecraft.compatibility.factions.extensions

import blue.sparse.minecraft.compatibility.factions.FactionsCompat
import com.massivecraft.factions.FPlayer
import com.massivecraft.factions.Faction
import com.massivecraft.factions.iface.RelationParticipator
import org.bukkit.OfflinePlayer

val OfflinePlayer.fPlayer: FPlayer
	get() {
		if (isOnline)
			return FactionsCompat.players.getByPlayer(player)

		return FactionsCompat.players.getByOfflinePlayer(this)
	}


var OfflinePlayer.faction: Faction
	get() = fPlayer.faction
	set(value) {
		fPlayer.faction = value
	}

var OfflinePlayer.fPower: Double
	get() = fPlayer.power
	set(value) {
		fPlayer.alterPower(value)
		fPlayer.updatePower()
	}

var OfflinePlayer.fPowerBoost: Double
	get() = fPlayer.powerBoost
	set(value) {
		fPlayer.powerBoost = value
	}

infix fun OfflinePlayer.relationTo(other: RelationParticipator) = fPlayer.getRelationTo(other)