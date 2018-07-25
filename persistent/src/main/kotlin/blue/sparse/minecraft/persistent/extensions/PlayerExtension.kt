package blue.sparse.minecraft.persistent.extensions

import blue.sparse.minecraft.persistent.PersistentModule
import blue.sparse.minecraft.persistent.persistents.PlayerPersistent
import org.bukkit.OfflinePlayer

val OfflinePlayer.persistent: PlayerPersistent
	get() = PersistentModule.getPlayerPersistent(uniqueId)