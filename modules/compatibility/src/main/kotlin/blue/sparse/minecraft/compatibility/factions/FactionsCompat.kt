package blue.sparse.minecraft.compatibility.factions

import blue.sparse.minecraft.compatibility.Compat
import com.massivecraft.factions.*

object FactionsCompat : Compat {
	val claims: Board by lazy { Board.getInstance() }
	val factions: Factions by lazy { Factions.getInstance() }
	val players: FPlayers by lazy { FPlayers.getInstance() }
}