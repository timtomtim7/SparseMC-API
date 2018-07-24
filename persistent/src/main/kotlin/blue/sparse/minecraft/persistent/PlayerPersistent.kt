package blue.sparse.minecraft.persistent

import blue.sparse.minecraft.core.data.nbt.Compound
import blue.sparse.minecraft.core.extensions.server
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.io.File
import java.util.UUID

class PlayerPersistent internal constructor(
		val id: UUID,
		compound: Compound
) : Persistent(compound) {

	val player: Player?
		get() = server.getPlayer(id)

	val offlinePlayer: OfflinePlayer
		get() = server.getOfflinePlayer(id)

	fun save() {
		val folder = File(PersistentModule.folder, "players")
		folder.mkdirs()

		val file = File(folder, "$id.dat")
		compound.write(file)
	}

	companion object {

		internal fun load(id: UUID): PlayerPersistent {
			val folder = File(PersistentModule.folder, "players")
			folder.mkdirs()

			val file = File(folder, "$id.dat")
			val compound = if (file.exists())
				Compound.read(file)
			else Compound()

			return PlayerPersistent(id, compound)
		}

	}

}