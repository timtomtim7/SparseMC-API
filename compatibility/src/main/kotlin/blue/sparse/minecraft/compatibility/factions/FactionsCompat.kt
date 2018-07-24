package blue.sparse.minecraft.compatibility.factions

import blue.sparse.minecraft.compatibility.Compat
import com.massivecraft.factions.Board
import com.massivecraft.factions.FPlayers
import com.massivecraft.factions.Factions

object FactionsCompat : Compat {
	val claims: Board by lazy { Board.getInstance() }
	val factions: Factions by lazy { Factions.getInstance() }
	val players: FPlayers by lazy { FPlayers.getInstance() }
}