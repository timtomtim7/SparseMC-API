package blue.sparse.minecraft.persistent.persistents

import blue.sparse.minecraft.core.data.nbt.Compound
import blue.sparse.minecraft.core.extensions.server
import blue.sparse.minecraft.persistent.Persistent
import blue.sparse.minecraft.persistent.PersistentModule
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

	fun save() = compound.write(File(folder, "$id.dat"))

	companion object {
		val folder get() = File(PersistentModule.folder, "players").apply { mkdirs() }

		internal fun load(id: UUID): PlayerPersistent {
			val file = File(folder, "$id.dat")
			val compound = if (file.exists())
				Compound.read(file)
			else Compound()

			return PlayerPersistent(id, compound)
		}

	}

}