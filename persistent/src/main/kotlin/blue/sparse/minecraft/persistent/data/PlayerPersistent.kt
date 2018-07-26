package blue.sparse.minecraft.persistent.data

import blue.sparse.minecraft.core.data.nbt.Compound
import blue.sparse.minecraft.persistent.PersistentPlugin
import org.bukkit.OfflinePlayer
import java.io.File
import java.util.WeakHashMap

class PlayerPersistent(
		manager: Manager,
		player: OfflinePlayer,
		override val compound: Compound
): Persistent<OfflinePlayer>(manager, player) {

	private val file = File(manager.folder, "${player.uniqueId}.dat")

	override fun save() {
		compound.write(file)
	}

	class Manager(plugin: PersistentPlugin): PersistentManager<OfflinePlayer>(plugin) {

		private val players = WeakHashMap<OfflinePlayer, PlayerPersistent>()

		override val folder: File
			get() = File(super.folder, "players").apply { mkdirs() }

		override fun get(value: OfflinePlayer): Persistent<OfflinePlayer> {
			return players.getOrPut(value) {
				val file = File(folder, "${value.uniqueId}.dat")
				PlayerPersistent(this, value, Compound.readOrCreate(file))
			}
		}

		override fun saveAll() = players.values.forEach(PlayerPersistent::save)

	}

}