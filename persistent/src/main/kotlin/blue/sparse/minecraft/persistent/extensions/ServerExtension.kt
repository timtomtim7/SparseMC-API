package blue.sparse.minecraft.persistent.extensions

import blue.sparse.minecraft.persistent.Persistent
import blue.sparse.minecraft.persistent.PersistentModule
import org.bukkit.Server

val Server.persistent: Persistent
	get() = PersistentModule.serverPersistent